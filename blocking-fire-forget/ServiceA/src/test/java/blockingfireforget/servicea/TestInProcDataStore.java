package blockingfireforget.servicea;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by vagrant on 04/06/17.
 */
public class TestInProcDataStore {

    @Test
    public void createBackingDataStore(){
        InProcDataStore dataStore = new InProcDataStore();
        Assert.assertNotNull(dataStore.getData());

    }

}
