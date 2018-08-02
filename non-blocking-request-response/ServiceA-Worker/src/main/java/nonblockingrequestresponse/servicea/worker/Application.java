package nonblockingrequestresponse.servicea.worker;

import com.mongodb.MongoClient;
import nonblockingrequestresponse.servicea.core.Configuration;
import nonblockingrequestresponse.servicea.infrastructure.mongodb.MongoConfiguration;
import nonblockingrequestresponse.servicea.core.ResourceService;
import nonblockingrequestresponse.servicea.infrastructure.mongodb.MongoResourceService;
import nonblockingrequestresponse.servicea.infrastructure.rabbitmq.RabbitMQRequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.lang.invoke.MethodHandles;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RabbitMQRequestProcessor processor;

    @Autowired
    public Application(Configuration configuration,
                       ResourceService resourceService){
        this.processor = new RabbitMQRequestProcessor(configuration, resourceService);
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration =
                Configuration.readConfiguration();

        System.setProperty("server.port", String.valueOf(configuration.getPort()));
        System.setProperty("server.address", configuration.getHostname());

        SpringApplication application = new SpringApplicationBuilder(Application.class)
                .build();

        ResourceService resourceService = createResourceService(configuration);

        ServiceBootstrapper bootstrapper = new ServiceBootstrapper(configuration, resourceService);
        application.addInitializers(bootstrapper);

        application.run(args);

    }

    @Override
    public void run(String... args) throws Exception {
        this.processor.run();
    }


    private static ResourceService createResourceService(Configuration configuration) {
        MongoConfiguration mongoConfiguration =
                configuration.getSubConfiguration(MongoConfiguration.CONFIG_NAME,
                        MongoConfiguration.class);
        MongoClient mongoClient = new MongoClient( mongoConfiguration.getHostname() ,
                mongoConfiguration.getPort() );
        return new MongoResourceService(mongoClient,
                mongoConfiguration.getDatabaseName(),
                mongoConfiguration.getCollectionName());
    }
}
