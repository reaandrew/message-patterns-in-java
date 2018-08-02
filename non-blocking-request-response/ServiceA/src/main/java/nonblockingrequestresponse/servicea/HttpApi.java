package nonblockingrequestresponse.servicea;

import com.mongodb.MongoClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import nonblockingrequestresponse.servicea.core.*;
import nonblockingrequestresponse.servicea.infrastructure.mongodb.MongoConfiguration;
import nonblockingrequestresponse.servicea.infrastructure.mongodb.MongoResourceQueryService;
import nonblockingrequestresponse.servicea.infrastructure.mongodb.MongoResourceQueryServiceFactory;
import nonblockingrequestresponse.servicea.infrastructure.rabbitmq.RabbitMQCommandDispatcherFactory;
import nonblockingrequestresponse.servicea.infrastructure.rabbitmq.RabbitMQConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.UUID;
import java.util.concurrent.TimeoutException;


@Controller
@SpringBootApplication
public class HttpApi {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ResourceQueryService queryService;
    private CommandDispatcher commandDispatcher;

    @Autowired
    public HttpApi(ResourceQueryService queryService,
                   CommandDispatcher commandDispatcher){
        this.queryService = queryService;
        this.commandDispatcher = commandDispatcher;
    }
    
    @RequestMapping(value = "/resources", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity publishEvent(@RequestBody CreateResourceDto data){

        ResponseEntity httpResponse;

        String id = UUID.randomUUID().toString();
        data.setId(id);

        try {
            this.commandDispatcher.dispatch(data);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, "/resources/"+id);
            httpResponse = new ResponseEntity(headers, HttpStatus.ACCEPTED);
        } catch (IOException e) {
            logger.error("Failed to send CreateResource instruction to RabbitMQ", e);
            httpResponse = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return httpResponse;
    }

    @RequestMapping(value = "/resources/{resourceId}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity getResource(@PathVariable("resourceId")  String resourceId){

        ResponseEntity httpResponse;
        try {
            Resource resource = this.queryService.getById(resourceId);
            httpResponse = new ResponseEntity<>(resource, HttpStatus.OK);
        } catch (ResourceServiceException e) {
            logger.error("Failed to getSubConfiguration resource", e);
            httpResponse = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return httpResponse;
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        Configuration configuration = Configuration.readConfiguration();

        System.setProperty("server.port", String.valueOf(configuration.getPort()));
        System.setProperty("server.address", configuration.getHostname());

        SpringApplication application = new SpringApplicationBuilder(HttpApi.class).build();

        ResourceQueryService queryService = new MongoResourceQueryServiceFactory(configuration).create();
        CommandDispatcher commandDispatcher = new RabbitMQCommandDispatcherFactory(configuration).create();

        ServiceBootstrapper bootstrapper = new ServiceBootstrapper(configuration, queryService, commandDispatcher);
        application.addInitializers(bootstrapper);

        application.run(args);
    }

}
