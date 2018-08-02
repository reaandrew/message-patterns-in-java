package nonblockingrequestresponse.servicea.infrastructure.rabbitmq;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import nonblockingrequestresponse.servicea.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class RabbitMQRequestProcessorConsumer extends DefaultConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final Configuration configuration;
    private final ResourceService resourceService;
    private final CommandExecutor commandExecutor;


    RabbitMQRequestProcessorConsumer(Channel channel,
                                     Configuration configuration,
                                     ResourceService resourceService) {
        super(channel);
        this.configuration = configuration;
        this.resourceService = resourceService;
        this.commandExecutor = new CommandExecutor(configuration, resourceService);
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body)
            throws IOException {
        String message = new String(body, "UTF-8");
        CreateResourceDto createResourceDto = new Gson().fromJson(message, CreateResourceDto.class);
        long deliveryTag = envelope.getDeliveryTag();
        MDC.put("eventID", createResourceDto.getId());

        logger.info("Processing Event");

        try {
            //handleResource(createResourceDto);

            this.commandExecutor.execute(createResourceDto);

            logger.info("Event Processed");
            this.getChannel().basicAck(deliveryTag, false);
        }catch(CommandHandlerException e){
            logger.error("Failed to publish createResourceDto", e);
            this.getChannel().basicNack(deliveryTag, false, true);
        }
    }


}
