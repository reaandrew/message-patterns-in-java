package nonblockingrequestresponse.servicea.infrastructure.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nonblockingrequestresponse.servicea.core.Resource;
import nonblockingrequestresponse.servicea.core.ResourceService;
import nonblockingrequestresponse.servicea.core.ResourceServiceException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;

public class MongoResourceService implements ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private MongoClient client;
    private final String databaseName;
    private final String collectionName;

    public MongoResourceService(MongoClient client, String databaseName,
                         String collectionName){
        this.client = client;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }

    @Override
    public void save(Resource resource) throws ResourceServiceException {
        MongoDatabase database = this.client.getDatabase(this.databaseName);

        MongoCollection<Document> collection =
                database.getCollection(this.collectionName);

        Document document = new Document();
        document.append("id", resource.getId());
        document.append("someProperty", resource.getSomeProperty());
        document.append("someOtherProperty", resource.getSomeOtherProperty());
        document.append("subResourceRef", resource.getSubResourceRef());

        collection.insertOne(document);

        MDC.put("eventId", resource.getId());
        logger.info("Event saved to MongoDB");
    }


}
