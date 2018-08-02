package nonblockingfireforget.servicea;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;


@Controller
@SpringBootApplication
public class HttpApi {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private NonBlockingFireForgetConfiguration configuration;
    private Channel channel;

    @Autowired
    public HttpApi(NonBlockingFireForgetConfiguration configuration,
                   Channel channel){
        this.configuration = configuration;

        this.channel = channel;
    }
    
    @RequestMapping(value = "/events", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity publishEvent(@RequestBody PublishEventDto data){

        ResponseEntity httpResponse;

        String id = UUID.randomUUID().toString();
        Event event = data.createEvent(id);
        RabbitMQConfiguration rabbitMQConfiguration = configuration.getRabbit();

        byte[] messageBodyBytes = new Gson().toJson(event).getBytes();
        try {
            this.channel.basicPublish(rabbitMQConfiguration.getServiceExchangeName(),
                    "",
                    new AMQP.BasicProperties(),
                    messageBodyBytes);
            logger.info("Event published to RabbitMQ");
            httpResponse = new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (IOException e) {
            logger.error("Failed to publish event to RabbitMQ", e);
            httpResponse = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return httpResponse;
    }

    private static NonBlockingFireForgetConfiguration getConfiguration() throws IOException {
        Gson gson = new Gson();

        NonBlockingFireForgetConfiguration configuration = new NonBlockingFireForgetConfiguration();

        Map<String, String> env = System.getenv();

        String configPath = env.get("config");

        MDC.put("configPath", configPath);
        if ( configPath != null && !configPath.isEmpty()){
            String config = new String(Files.readAllBytes(Paths.get(configPath)),
                    StandardCharsets.UTF_8);
            configuration = gson.fromJson(config, NonBlockingFireForgetConfiguration.class);

            MDC.put("config", config);
            logger.info("NonBlockingFireForgetConfiguration loaded from environment");
        }else{
            logger.info("NonBlockingFireForgetConfiguration not loaded {}", configPath);
        }

        return configuration;
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        NonBlockingFireForgetConfiguration configuration = getConfiguration();
        logger.info("NonBlockingFireForgetConfiguration ready");

        System.setProperty("server.port", String.valueOf(configuration.getPort()));
        System.setProperty("server.address", configuration.getHostname());

        SpringApplication application = new SpringApplicationBuilder(HttpApi.class)
                .build();

        EventService eventService = createEventService(configuration);

        RabbitMQEventProcessor processor =
                new RabbitMQEventProcessor(configuration, eventService);

        new Thread(processor).start();

        Channel publisherChannel = createPublisherChannel(configuration);
        ServiceAInitializer dependencyInitializer = new ServiceAInitializer(configuration, eventService, publisherChannel);
        application.addInitializers(dependencyInitializer);

        application.run(args);
    }

    private static Channel createPublisherChannel(NonBlockingFireForgetConfiguration configuration) throws IOException, TimeoutException {
        RabbitMQConfiguration rabbitMQConfiguration = configuration.getRabbit();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost(rabbitMQConfiguration.getServiceVirtualHost());
        factory.setHost(rabbitMQConfiguration.getHostname());
        factory.setPort(rabbitMQConfiguration.getPort());
        factory.setUsername(rabbitMQConfiguration.getUsername());
        factory.setPassword(rabbitMQConfiguration.getPassword());

        Connection publisherConnection = factory.newConnection();
        return publisherConnection.createChannel();
    }

    private static EventService createEventService(NonBlockingFireForgetConfiguration configuration) {
        MongoConfiguration mongoConfiguration = configuration.getMongo();
        MongoClient mongoClient = new MongoClient( mongoConfiguration.getHostname() , mongoConfiguration.getPort() );
        return new MongoEventService(mongoClient,
                mongoConfiguration.getDatabaseName(),
                mongoConfiguration.getCollectionName());
    }
}
