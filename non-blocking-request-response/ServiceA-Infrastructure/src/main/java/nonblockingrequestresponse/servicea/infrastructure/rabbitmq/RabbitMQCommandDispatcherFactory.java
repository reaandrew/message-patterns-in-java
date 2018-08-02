package nonblockingrequestresponse.servicea.infrastructure.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import nonblockingrequestresponse.servicea.core.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQCommandDispatcherFactory {
    private Configuration configuration;

    public RabbitMQCommandDispatcherFactory(Configuration configuration){
        this.configuration = configuration;
    }

    public RabbitMQCommandDispatcher create() throws IOException, TimeoutException {
        RabbitMQConfiguration rabbitMQConfiguration =
                configuration.getSubConfiguration(RabbitMQConfiguration.CONFIG_NAME,
                        RabbitMQConfiguration.class);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost(rabbitMQConfiguration.getServiceVirtualHost());
        factory.setHost(rabbitMQConfiguration.getHostname());
        factory.setPort(rabbitMQConfiguration.getPort());
        factory.setUsername(rabbitMQConfiguration.getUsername());
        factory.setPassword(rabbitMQConfiguration.getPassword());


        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        return new RabbitMQCommandDispatcher(channel, rabbitMQConfiguration);
    }
}
