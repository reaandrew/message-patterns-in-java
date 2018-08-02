package nonblockingfireforget.servicea;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;

public class MongoEventService implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private MongoClient client;
    private final String databaseName;
    private final String collectionName;

    MongoEventService(MongoClient client, String databaseName,
                      String collectionName){
        this.client = client;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }

    @Override
    public void publish(Event event) throws PublishException {
        MongoDatabase database = this.client.getDatabase(this.databaseName);

        MongoCollection<Document> eventCollection =
                database.getCollection(this.collectionName);

        Document eventDocument = new Document();
        eventDocument.append("id", event.getId());
        eventDocument.append("someProperty", event.getSomeProperty());
        eventDocument.append("someOtherProperty", event.getSomeOtherProperty());

        eventCollection.insertOne(eventDocument);

        MDC.put("eventId", event.getId());
        logger.info("Event saved to MongoDB");
    }
}
