package nonblockingrequestresponse.servicea.core;

import java.io.IOException;

public interface CommandDispatcher {
    <TCommand> void dispatch(TCommand command) throws IOException;
}
