package nonblockingrequestresponse.servicea.core;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestConfiguration {

    @Test
    public void loadsSubConfiguration() throws IOException {
        String jsonConfig = "{\"fake\":{\"something\":\"fubar\"}}";

        Configuration config = new Configuration(jsonConfig);

        FakeConfig fake = config.getSubConfiguration("fake", FakeConfig.class);
        Assert.assertThat(fake.getSomething(), Is.is("fubar"));
    }

}
