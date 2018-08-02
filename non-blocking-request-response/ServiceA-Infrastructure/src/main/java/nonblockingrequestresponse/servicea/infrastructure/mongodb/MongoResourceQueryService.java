package nonblockingrequestresponse.servicea.infrastructure.mongodb;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import nonblockingrequestresponse.servicea.core.Resource;
import nonblockingrequestresponse.servicea.core.ResourceQueryService;
import nonblockingrequestresponse.servicea.core.ResourceServiceException;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;

import static com.mongodb.client.model.Filters.eq;

public class MongoResourceQueryService implements ResourceQueryService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private MongoClient client;
    private final String databaseName;
    private final String collectionName;

    public MongoResourceQueryService(MongoClient client, String databaseName,
                                     String collectionName){
        this.client = client;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }

    @Override
    public Resource getById(String resourceId) throws ResourceServiceException {
        MongoDatabase database = this.client.getDatabase(this.databaseName);

        MongoCollection<Document> collection =
                database.getCollection(this.collectionName);
        Document document = collection.find(eq("id", resourceId)).first();

        MDC.put("document", new Gson().toJson(document));
        logger.info("getById");

        return new Resource(document.getString("id"),
                document.getString("someProperty"),
                document.getString("someOtherProperty"),
                document.getString("subResourceRef"));
    }

}
