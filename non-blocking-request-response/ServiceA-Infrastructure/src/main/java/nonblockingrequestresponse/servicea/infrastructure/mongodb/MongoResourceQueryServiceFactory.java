package nonblockingrequestresponse.servicea.infrastructure.mongodb;

import com.mongodb.MongoClient;
import nonblockingrequestresponse.servicea.core.Configuration;

public class MongoResourceQueryServiceFactory {

    private Configuration configuration;

    public MongoResourceQueryServiceFactory(Configuration configuration){

        this.configuration = configuration;
    }

    public MongoResourceQueryService create(){
        MongoConfiguration mongoConfiguration =
                configuration.getSubConfiguration(MongoConfiguration.CONFIG_NAME,
                        MongoConfiguration.class);
        MongoClient mongoClient = new MongoClient( mongoConfiguration.getHostname() , mongoConfiguration.getPort() );
        return new MongoResourceQueryService(mongoClient,
                mongoConfiguration.getDatabaseName(),
                mongoConfiguration.getCollectionName());
    }
}
