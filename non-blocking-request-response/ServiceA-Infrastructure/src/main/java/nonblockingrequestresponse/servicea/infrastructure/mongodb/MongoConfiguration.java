package nonblockingrequestresponse.servicea.infrastructure.mongodb;

public class MongoConfiguration{
    public static final String CONFIG_NAME = "mongodb";

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
