package blockingrequestresponse.servicea;

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
import org.springframework.http.HttpHeaders;
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

    private final ResourceService service;
    private BlockingRequestResponseConfiguration configuration;

    @Autowired
    public HttpApi(ResourceService service,
                   BlockingRequestResponseConfiguration configuration){
        this.service = service;
        this.configuration = configuration;
    }
    
    @RequestMapping(value = "/resources", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity createResource(@RequestBody CreateResourceDto data){
            ResponseEntity httpResponse;

            Gson gson = new Gson();
            try {
                HttpResponse<String> response = Unirest.post(this.configuration.getServiceUrl())
                        .header("Content-Type", "application/json")
                        .body(gson.toJson(data)).asString();

                String subResourceRef = response.getHeaders().get(HttpHeaders.LOCATION);

                String id = UUID.randomUUID().toString();

                Resource resource = new Resource(id,
                        data.getSomeProperty(),
                        data.getSomeOtherProperty(),
                        subResourceRef);

                httpResponse = createResource(id, resource);
            } catch (UnirestException e) {
                logger.error("Failed to communicate with serviceB", e);
                httpResponse = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }


        return httpResponse;
    }

    private ResponseEntity createResource(String id, Resource resource) {
        ResponseEntity httpResponse;
        try {
            this.service.save(resource);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.LOCATION, "/resources/"+id);
            httpResponse = new ResponseEntity(headers, HttpStatus.CREATED);

            logger.info("Resource created");
        } catch (ResourceServiceException e) {
            logger.error("Failed to save resource", e);
            httpResponse = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return httpResponse;
    }

    @RequestMapping(value = "/resources/{resourceId}", method = RequestMethod.GET)
    @ResponseBody
    ResponseEntity getResource(@PathVariable("resourceId")  String resourceId){

        ResponseEntity httpResponse;
        try {
            Resource resource = this.service.getById(resourceId);
            httpResponse = new ResponseEntity<>(resource, HttpStatus.OK);
        } catch (ResourceServiceException e) {
            logger.error("Failed to get resource", e);
            httpResponse = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return httpResponse;
    }

    public static void main(String[] args) throws Exception {
        BlockingRequestResponseConfiguration configuration = getConfiguration();

        System.setProperty("server.port", String.valueOf(configuration.getPort()));
        System.setProperty("server.address", configuration.getHostname());

        SpringApplication application = new SpringApplicationBuilder(HttpApi.class)
                .build();

        application.addInitializers((ApplicationContextInitializer<ConfigurableApplicationContext>) applicationContext ->
                applicationContext.getBeanFactory().registerResolvableDependency(BlockingRequestResponseConfiguration.class, configuration));

        application.run(args);
    }

    private static BlockingRequestResponseConfiguration getConfiguration() throws IOException {
        BlockingRequestResponseConfiguration configuration = new BlockingRequestResponseConfiguration();

        Gson gson = new Gson();

        Map<String, String> env = System.getenv();

        String configPath = env.get("config");
        if ( configPath != null && !configPath.isEmpty()){
            String config = new String(Files.readAllBytes(Paths.get(configPath)),
                    StandardCharsets.UTF_8);
            configuration = gson.fromJson(config, BlockingRequestResponseConfiguration.class);

            //Structured Logging needed!

            MDC.put("hostname", configuration.getHostname());
            MDC.put("port", String.valueOf(configuration.getPort()));
            MDC.put("serviceBUrl", configuration.getServiceUrl());
            logger.info("BlockingRequestResponseConfiguration loaded from environment {}", config);
        }else{
            logger.info("BlockingRequestResponseConfiguration not loaded {}", configPath);
        }
        return configuration;
    }
}
