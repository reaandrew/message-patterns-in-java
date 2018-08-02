package nonblockingfireforget.servicea;

@FunctionalInterface
public interface EventService {
    void publish(Event event) throws PublishException;
}
