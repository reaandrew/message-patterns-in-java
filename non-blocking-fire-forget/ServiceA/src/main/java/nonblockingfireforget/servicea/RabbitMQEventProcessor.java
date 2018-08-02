package nonblockingfireforget.servicea;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeoutException;


class RabbitMQEventProcessorConsumer extends DefaultConsumer{

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final NonBlockingFireForgetConfiguration configuration;
    private final EventService eventService;


    RabbitMQEventProcessorConsumer(Channel channel, NonBlockingFireForgetConfiguration configuration, EventService eventService) {
        super(channel);
        this.configuration = configuration;
        this.eventService = eventService;
    }

    @Override
    public void handleDelivery(String consumerTag,
                               Envelope envelope,
                               AMQP.BasicProperties properties,
                               byte[] body)
            throws IOException {
        String message = new String(body, "UTF-8");
        Event event = new Gson().fromJson(message, Event.class);
        long deliveryTag = envelope.getDeliveryTag();
        MDC.put("eventID", event.getId());

        logger.info("Processing Event");

        try {
            handleEvent(event);
            logger.info("Event Processed");
            this.getChannel().basicAck(deliveryTag, false);
        }catch(PublishException e){
            logger.error("Failed to publish event", e);
            this.getChannel().basicNack(deliveryTag, false, true);
        }
    }


    private void handleEvent(Event event) throws PublishException {
        eventService.publish(event);
        logger.info("Event published");

        //The next example should show how the following is bad practice and
        //an event driven approach leads to a more extensible architecture.
        //In this case it can also produce a more resilient solution separating the
        //concerns of handling the event from the publication of the event handling.
        notifyServiceB(event);
    }

    private void notifyServiceB(Event event) {
        //This is an intentional bad example of breaking the OpenClose principle.
        try {
            HttpResponse<String> response = Unirest.post(configuration.getServiceUrl())
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
}

public class RabbitMQEventProcessor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final NonBlockingFireForgetConfiguration configuration;
    private final EventService eventService;

    RabbitMQEventProcessor(NonBlockingFireForgetConfiguration configuration,
                           EventService eventService){
        this.configuration = configuration;

        this.eventService = eventService;
    }

    @Override
    public void run() {
        try {
            //Start a RabbitMQ consumer
            RabbitMQConfiguration rabbitMQConfiguration = configuration.getRabbit();

            Channel channel = createChannel(rabbitMQConfiguration);

            Consumer consumer = new RabbitMQEventProcessorConsumer(channel,
                    this.configuration,
                    this.eventService);

            channel.basicConsume(rabbitMQConfiguration.getServiceQueueName(),
                    false,
                    rabbitMQConfiguration.getServiceConsumerTag(),
                    consumer);

        } catch (IOException e) {
            logger.error("Cannot connect to RabbitMQ", e);
        } catch (TimeoutException e) {
            logger.error("Timeout connecting to RabbitMQ", e);
        }
    }


    private Channel createChannel(RabbitMQConfiguration rabbitMQConfiguration) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost(rabbitMQConfiguration.getServiceVirtualHost());
        factory.setHost(rabbitMQConfiguration.getHostname());
        factory.setPort(rabbitMQConfiguration.getPort());
        factory.setUsername(rabbitMQConfiguration.getUsername());
        factory.setPassword(rabbitMQConfiguration.getPassword());


        Connection conn = factory.newConnection();
        return conn.createChannel();
    }
}
