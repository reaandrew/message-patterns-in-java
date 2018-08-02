package blockingrequestresponse.servicea;

public interface ResourceService {
    void save(Resource resource) throws ResourceServiceException;
    Resource getById(String resourceId) throws ResourceServiceException;
}
