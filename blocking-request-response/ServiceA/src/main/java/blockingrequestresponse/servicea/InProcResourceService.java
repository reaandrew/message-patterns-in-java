package blockingrequestresponse.servicea;

import org.springframework.stereotype.Service;

@Service
public class InProcResourceService implements ResourceService {

    private final InProcDataStore dataStore;

    InProcResourceService(InProcDataStore data){
        this.dataStore = data;
    }

    @Override
    public void save(Resource resource) throws ResourceServiceException {
        this.dataStore.getData().put(resource.getId(), resource);
    }

    @Override
    public Resource getById(String resourceId) throws ResourceServiceException {
        return this.dataStore.getData().get(resourceId);
    }
}
