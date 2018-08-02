package nonblockingrequestresponse.servicea.infrastructure.rabbitmq;

public class RabbitMQConfiguration {
    public static final String CONFIG_NAME = "rabbitmq";

    private String username;
    private String password;
    private String hostname;
    private int port;
    private String serviceVirtualHost;
    private String serviceQueueName;
    private String serviceExchangeName;
    private String serviceConsumerTag;

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServiceQueueName() {
        return serviceQueueName;
    }

    public String getServiceExchangeName() {
        return serviceExchangeName;
    }

    public String getServiceConsumerTag() {
        return serviceConsumerTag;
    }

    public String getServiceVirtualHost() {
        return serviceVirtualHost;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServiceVirtualHost(String serviceVirtualHost) {
        this.serviceVirtualHost = serviceVirtualHost;
    }

    public void setServiceQueueName(String serviceQueueName) {
        this.serviceQueueName = serviceQueueName;
    }

    public void setServiceExchangeName(String serviceExchangeName) {
        this.serviceExchangeName = serviceExchangeName;
    }

    public void setServiceConsumerTag(String serviceConsumerTag) {
        this.serviceConsumerTag = serviceConsumerTag;
    }
}
