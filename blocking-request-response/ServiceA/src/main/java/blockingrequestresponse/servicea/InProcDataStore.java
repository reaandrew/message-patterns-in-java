package blockingrequestresponse.servicea;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class InProcDataStore {

    private final ConcurrentHashMap<String, Resource> data;

    InProcDataStore(){
        data = new ConcurrentHashMap<>();
    }

    ConcurrentHashMap<String, Resource> getData() {
        return data;
    }
}
