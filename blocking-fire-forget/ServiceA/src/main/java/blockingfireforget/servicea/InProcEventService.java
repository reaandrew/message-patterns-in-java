package blockingfireforget.servicea;

import org.springframework.stereotype.Service;

@Service
public class InProcEventService implements EventService {

    private final InProcDataStore dataStore;

    InProcEventService(InProcDataStore data){
        this.dataStore = data;
    }

    @Override
    public void Publish(Event event) throws PublishException {
        this.dataStore.getData().put(event.getId(), event);
    }
}
