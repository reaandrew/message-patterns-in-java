package nonblockingrequestresponse.servicea.infrastructure.rabbitmq;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import nonblockingrequestresponse.servicea.core.CommandDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class RabbitMQCommandDispatcher implements CommandDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Channel channel;
    private RabbitMQConfiguration configuration;

    public RabbitMQCommandDispatcher(Channel channel,
                                     RabbitMQConfiguration configuration){

        this.channel = channel;
        this.configuration = configuration;
    }

    @Override
    public <TCommand> void dispatch(TCommand command) {
        byte[] messageBodyBytes = new Gson().toJson(command).getBytes();

        try {
            this.channel.basicPublish(this.configuration.getServiceExchangeName(),
                    "",
                    new AMQP.BasicProperties(),
                    messageBodyBytes);
            logger.info("CreateResource instruction sent to RabbitMQ");

        } catch (IOException e) {
            logger.error("Failed to publish to RabbitMQ", e);
        }

    }
}
