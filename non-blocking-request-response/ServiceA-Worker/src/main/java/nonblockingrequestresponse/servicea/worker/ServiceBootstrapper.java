package nonblockingrequestresponse.servicea.worker;

import nonblockingrequestresponse.servicea.core.Configuration;
import nonblockingrequestresponse.servicea.core.ResourceService;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ServiceBootstrapper implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final Configuration configuration;
    private final ResourceService resourceService;

    public ServiceBootstrapper(Configuration configuration, ResourceService resourceService) {
        this.configuration = configuration;
        this.resourceService = resourceService;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().registerResolvableDependency(Configuration.class, configuration);
        applicationContext.getBeanFactory().registerResolvableDependency(ResourceService.class, resourceService);
    }
}
