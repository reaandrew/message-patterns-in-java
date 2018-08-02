package blockingfireforget.servicea;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by vagrant on 04/06/17.
 */
public class TestPublishEventRequest {

    @Test
    public void createsEventFromRequest(){
        String expectedSomeProperty = "Something";
        String expectedSomeOtherProperty = "Something Else";
        String expectedId = "someID";

        PublishEventDto request =
                new PublishEventDto(expectedSomeProperty, expectedSomeOtherProperty);

        Event expected = new Event(expectedId, expectedSomeProperty, expectedSomeOtherProperty);

        Assert.assertThat(request.createEvent(expectedId), Is.is(expected));
    }

}
