package blockingfireforget.servicea;

@FunctionalInterface
public interface EventService {
    void Publish(Event event) throws PublishException;
}
