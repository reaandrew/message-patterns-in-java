package blockingfireforget.servicea;

import com.google.gson.Gson;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;


@Controller
@SpringBootApplication
public class HttpApi {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EventService eventService;
    private BlockingFireForgetConfiguration configuration;

    @Autowired
    public HttpApi(EventService service,
                   BlockingFireForgetConfiguration configuration){
        this.eventService = service;
        this.configuration = configuration;
    }
    
    @RequestMapping(value = "/events", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity publishEvent(@RequestBody PublishEventDto data){

        ResponseEntity httpResponse;

        try {
            String id = UUID.randomUUID().toString();
            Event event = data.createEvent(id);
            this.eventService.Publish(event);

            notifyServiceB(event);

            logger.info("Event publication accepted");
            httpResponse = new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (PublishException e) {
            logger.error("Failed to publish event", e);
            httpResponse = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return httpResponse;
    }

    private void notifyServiceB(Event event) {
        //This is an intentional bad example of breaking the OpenClose principle.
        try {
            HttpResponse<String> response = Unirest.post(this.configuration.getServiceUrl())
                    .header("Content-Type", "application/json")
                    .body(new Gson().toJson(event)).asString();

            if (response.getCode() != HttpStatus.ACCEPTED.value()){
                MDC.put("HTTP_STATUS", String.valueOf(response.getCode()));
                logger.error("Unknown code received from ServiceB");
            }

        } catch (UnirestException e) {
            logger.error("Error communicating with ServiceB", e);
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication application = new SpringApplicationBuilder(HttpApi.class).build();

        BlockingFireForgetConfiguration configuration = getConfiguration();

        System.setProperty("server.port", String.valueOf(configuration.getPort()));
        System.setProperty("server.address", configuration.getHostname());

        application.addInitializers((ApplicationContextInitializer<ConfigurableApplicationContext>) applicationContext ->
                applicationContext.getBeanFactory().registerResolvableDependency(BlockingFireForgetConfiguration.class, configuration));

        application.run(args);
    }

    private static BlockingFireForgetConfiguration getConfiguration() throws IOException {
        Gson gson = new Gson();

        BlockingFireForgetConfiguration configuration = new BlockingFireForgetConfiguration();

        Map<String, String> env = System.getenv();

        String configPath = env.get("config");
        if ( configPath != null && !configPath.isEmpty()){
            String config = new String(Files.readAllBytes(Paths.get(configPath)),
                    StandardCharsets.UTF_8);
            configuration = gson.fromJson(config, BlockingFireForgetConfiguration.class);

            MDC.put("SomeKey", "SomeValue");
            logger.info("BlockingFireForgetConfiguration loaded from environment {}", config);
        }else{
            logger.info("BlockingFireForgetConfiguration not loaded {}", configPath);
        }
        return configuration;
    }
}
