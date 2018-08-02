package nonblockingrequestresponse.servicea.core;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;

public class CreateResourceCommandHandler implements CommandHandler<CreateResourceDto> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Configuration configuration;
    private ResourceService resourceService;

    public CreateResourceCommandHandler(Configuration configuration,
                                        ResourceService resourceService){
        this.configuration = configuration;

        this.resourceService = resourceService;
    }

    @Override
    public void execute(CreateResourceDto createResourceDto) {
        try {
            Gson gson = new Gson();
            HttpResponse<String> response = Unirest.post(this.configuration.getServiceUrl())
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(createResourceDto)).asString();

            String subResourceRef = response.getHeaders().get("location");

            MDC.put("response-headers", gson.toJson(response.getHeaders()));
            logger.info("Response from Sub-resource Service");

            Resource resource = new Resource(createResourceDto.getId(),
                    createResourceDto.getSomeProperty(),
                    createResourceDto.getSomeOtherProperty(),
                    subResourceRef);

            this.resourceService.save(resource);

        } catch (UnirestException e) {
            logger.error("Failed to communicate with serviceB", e);
        } catch (ResourceServiceException e) {
            logger.error("Failed to create create resource", e);
        }
    }
}
