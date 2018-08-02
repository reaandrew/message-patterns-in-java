package nonblockingrequestresponse.servicea.core;

public interface CommandHandler<TCommand> {

    void execute(TCommand command) throws CommandHandlerException;

}
