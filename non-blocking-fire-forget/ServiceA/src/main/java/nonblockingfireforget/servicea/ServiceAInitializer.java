package nonblockingfireforget.servicea;

import com.rabbitmq.client.Channel;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ServiceAInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final NonBlockingFireForgetConfiguration configuration;
    private final EventService eventService;
    private Channel publisherChannel;

    ServiceAInitializer(NonBlockingFireForgetConfiguration configuration,
                               EventService eventService,
                               Channel publisherChannel){
        this.configuration = configuration;
        this.eventService = eventService;
        this.publisherChannel = publisherChannel;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.getBeanFactory().registerResolvableDependency(NonBlockingFireForgetConfiguration.class, configuration);
        applicationContext.getBeanFactory().registerResolvableDependency(EventService.class, eventService);
        applicationContext.getBeanFactory().registerResolvableDependency(Channel.class, publisherChannel);
    }
}
