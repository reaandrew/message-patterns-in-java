package nonblockingrequestresponse.servicea.infrastructure.mongodb;

import com.google.gson.Gson;
import com.rabbitmq.client.*;
import nonblockingrequestresponse.servicea.core.CommandDispatcher;
import nonblockingrequestresponse.servicea.core.Configuration;
import nonblockingrequestresponse.servicea.core.CreateResourceDto;
import nonblockingrequestresponse.servicea.infrastructure.rabbitmq.RabbitMQCommandDispatcherFactory;
import nonblockingrequestresponse.servicea.infrastructure.rabbitmq.RabbitMQConfiguration;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestRabbitMQCommandDispatcher {

    private static final String ROUTING_KEY = "";

    @Test
    public void dispatchesCommand() throws IOException, TimeoutException, InterruptedException {
        RabbitMQConfiguration rabbitMQConfiguration = new RabbitMQConfiguration();
        rabbitMQConfiguration.setPort(5673);
        rabbitMQConfiguration.setHostname("127.0.0.1");
        rabbitMQConfiguration.setServiceConsumerTag("test");
        rabbitMQConfiguration.setServiceExchangeName("test");
        rabbitMQConfiguration.setServiceQueueName("TestRabbitMQCommandDispatcher");
        rabbitMQConfiguration.setUsername("guest");
        rabbitMQConfiguration.setPassword("guest");
        rabbitMQConfiguration.setServiceVirtualHost("/");

        Configuration configuration = new Configuration();
        configuration.addSubConfiguration(RabbitMQConfiguration.CONFIG_NAME, rabbitMQConfiguration);

        Channel channel = setupRabbitMQ(rabbitMQConfiguration);

        CountDownLatch latch = new CountDownLatch(1);
        try {
            CreateResourceDto data = new CreateResourceDto();
            data.setId("id");
            data.setSomeProperty("property");
            data.setSomeOtherProperty("somethingElse");

            CommandDispatcher dispatcher = new RabbitMQCommandDispatcherFactory(configuration).create();
            dispatcher.dispatch(data);

            channel.basicConsume(rabbitMQConfiguration.getServiceQueueName(), new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    CreateResourceDto obj = new Gson().fromJson(message, CreateResourceDto.class);

                    Assert.assertThat(obj.getId(), Is.is(data.getId()));
                    latch.countDown();
                }
            });


            latch.await(1, TimeUnit.SECONDS);
        }
        finally {
            tearDownRabbitMQ(rabbitMQConfiguration, channel);
        }

    }

    private void tearDownRabbitMQ(RabbitMQConfiguration rabbitMQConfiguration, Channel channel) throws IOException {
        channel.queueUnbind(rabbitMQConfiguration.getServiceQueueName(),
                rabbitMQConfiguration.getServiceExchangeName(),
                ROUTING_KEY);
        channel.queueDelete(rabbitMQConfiguration.getServiceQueueName());
        channel.exchangeDelete(rabbitMQConfiguration.getServiceExchangeName());
    }

    private Channel setupRabbitMQ(RabbitMQConfiguration rabbitMQConfiguration) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost(rabbitMQConfiguration.getServiceVirtualHost());
        factory.setHost(rabbitMQConfiguration.getHostname());
        factory.setPort(rabbitMQConfiguration.getPort());
        factory.setUsername(rabbitMQConfiguration.getUsername());
        factory.setPassword(rabbitMQConfiguration.getPassword());

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(rabbitMQConfiguration.getServiceExchangeName(), "topic");
        channel.queueDeclare(rabbitMQConfiguration.getServiceQueueName(),false,true, true, null);
        channel.queueBind(rabbitMQConfiguration.getServiceQueueName(), rabbitMQConfiguration.getServiceExchangeName(),ROUTING_KEY);
        return channel;
    }

}
