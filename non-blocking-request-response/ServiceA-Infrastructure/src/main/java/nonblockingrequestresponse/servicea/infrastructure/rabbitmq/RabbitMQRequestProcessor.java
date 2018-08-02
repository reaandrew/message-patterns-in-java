package nonblockingrequestresponse.servicea.infrastructure.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import nonblockingrequestresponse.servicea.core.Configuration;
import nonblockingrequestresponse.servicea.core.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeoutException;


public class RabbitMQRequestProcessor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Configuration configuration;
    private final ResourceService resourceService;

    public RabbitMQRequestProcessor(Configuration configuration,
                                    ResourceService eventService){
        this.configuration = configuration;

        this.resourceService = eventService;
    }

    @Override
    public void run() {
        try {
            //Start a RabbitMQ consumer
            RabbitMQConfiguration rabbitMQConfiguration =
                    configuration.getSubConfiguration(RabbitMQConfiguration.CONFIG_NAME,
                            RabbitMQConfiguration.class);

            Channel channel = createChannel(rabbitMQConfiguration);

            Consumer consumer = new RabbitMQRequestProcessorConsumer(channel,
                    this.configuration,
                    this.resourceService);

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
