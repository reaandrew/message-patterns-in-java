package nonblockingrequestresponse.servicea.core;

public interface ResourceQueryService {
    Resource getById(String resourceId) throws ResourceServiceException;
}
