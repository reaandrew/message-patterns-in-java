package nonblockingrequestresponse.servicea.core;

public class CommandHandlerException extends Exception {

    public CommandHandlerException(Throwable innerException){
        super(innerException);
    }
}
