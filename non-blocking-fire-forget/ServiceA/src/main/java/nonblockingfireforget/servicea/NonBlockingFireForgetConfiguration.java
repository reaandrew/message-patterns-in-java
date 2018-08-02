package nonblockingfireforget.servicea;

class MongoConfiguration{
    private String databaseName;
    private String collectionName;
    private String hostname;
    private int port;

    public String getDatabaseName() {
        return databaseName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }
}

class RabbitMQConfiguration {
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
}

public class NonBlockingFireForgetConfiguration {


    private String hostname;

    private int port;

    private String serviceUrl;

    private MongoConfiguration mongo;

    private RabbitMQConfiguration rabbit;

    public NonBlockingFireForgetConfiguration(){
        //Used for serialization purposes
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public RabbitMQConfiguration getRabbit() {
        return rabbit;
    }

    public MongoConfiguration getMongo() {
        return mongo;
    }
}
