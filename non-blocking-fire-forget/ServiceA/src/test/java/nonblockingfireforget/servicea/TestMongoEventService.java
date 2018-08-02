package nonblockingfireforget.servicea;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by vagrant on 12/06/17.
 */
public class TestMongoEventService {

    @Test
    //We need a strongly typed way of doing this rather than mapping maps!
    //TODO: review the use of MongoDB Java Driver Codecs and Custom Classes for mapping
    //Prefer POJO over annotations.
    public void savesEventToDataBase() throws PublishException {

        String databaseName = UUID.randomUUID().toString();
        String collectionName = "someCollection";

        MongoClient mongoClient = new MongoClient( "127.0.0.1" , 27017 );
        MongoEventService service = new MongoEventService(mongoClient, databaseName, collectionName);

        Event event = new Event("id", "A", "B");

        service.publish(event);

        //Find the new record.
        MongoDatabase db = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = db.getCollection(collectionName);
        Document eventDocument = collection.find(eq("id", event.getId())).first();
        Assert.assertThat(eventDocument.get("id"), Is.is(event.getId()));
        Assert.assertThat(eventDocument.get("someProperty"), Is.is(event.getSomeProperty()));
        Assert.assertThat(eventDocument.get("someOtherProperty"), Is.is(event.getSomeOtherProperty()));
    }

}
