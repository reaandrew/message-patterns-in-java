package blockingfireforget.servicea;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by vagrant on 04/06/17.
 */
public class TestInProcEventService {

    @Test
    public void createsAnEvent() throws PublishException {
        InProcDataStore dataStore = new InProcDataStore();
        InProcEventService service = new InProcEventService(dataStore);

        Event event = new Event("SomeID",
                "SomeProperty",
                "SomeOtherProperty");

        service.Publish(event);

        Event savedEvent = dataStore.getData().get(event.getId());
        Assert.assertThat(savedEvent, Is.is(event));
    }

}
