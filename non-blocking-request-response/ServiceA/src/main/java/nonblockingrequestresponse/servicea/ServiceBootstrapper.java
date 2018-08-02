package nonblockingrequestresponse.servicea;

import nonblockingrequestresponse.servicea.core.CommandDispatcher;
import nonblockingrequestresponse.servicea.core.Configuration;
import nonblockingrequestresponse.servicea.core.ResourceQueryService;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ServiceBootstrapper implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final Configuration configuration;
    private final ResourceQueryService resourceQueryService;
    private CommandDispatcher commandDispatcher;

    ServiceBootstrapper(Configuration configuration,
                        ResourceQueryService queryService,
                        CommandDispatcher commandDispatcher) {
        this.configuration = configuration;
        this.resourceQueryService = queryService;
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().registerResolvableDependency(Configuration.class, configuration);
        applicationContext.getBeanFactory().registerResolvableDependency(ResourceQueryService.class, resourceQueryService);
        applicationContext.getBeanFactory().registerResolvableDependency(CommandDispatcher.class, commandDispatcher);
    }
}
