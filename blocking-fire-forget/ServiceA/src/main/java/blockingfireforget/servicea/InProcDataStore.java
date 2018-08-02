package blockingfireforget.servicea;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class InProcDataStore {

    private final ConcurrentHashMap<String, Event> data;

    InProcDataStore(){
        data = new ConcurrentHashMap<>();
    }

    ConcurrentHashMap<String, Event> getData() {
        return data;
    }
}
