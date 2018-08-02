package blockingrequestresponse.servicea;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class TestInProcResourceService {

    @Test
    public void createsAnEvent() throws ResourceServiceException {
        InProcDataStore dataStore = new InProcDataStore();
        InProcResourceService service = new InProcResourceService(dataStore);

        Resource resource = new Resource("SomeID",
                "SomeProperty",
                "SomeOtherProperty",
                "SomeResourceID");

        service.save(resource);

        Resource savedResource = dataStore.getData().get(resource.getId());
        Assert.assertThat(savedResource, Is.is(resource));
    }

}
