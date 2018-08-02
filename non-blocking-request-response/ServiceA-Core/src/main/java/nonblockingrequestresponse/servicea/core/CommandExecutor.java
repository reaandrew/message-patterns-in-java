package nonblockingrequestresponse.servicea.core;

import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {
    private final Map<Class, CommandHandler> handlers;

    public CommandExecutor(Configuration configuration,
                           ResourceService resourceService){
        this.handlers = new HashMap<>();
        this.handlers.put(CreateResourceDto.class,
                new CreateResourceCommandHandler(configuration, resourceService));
    }

    public <TCommand> void execute(TCommand command) throws CommandHandlerException {
        this.handlers.get(command.getClass()).execute(command);
    }
}
