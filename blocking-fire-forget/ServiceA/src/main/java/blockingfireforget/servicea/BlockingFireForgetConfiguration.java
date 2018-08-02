package blockingfireforget.servicea;

public class BlockingFireForgetConfiguration {

    private String hostname;

    private int port;

    private String serviceUrl;

    BlockingFireForgetConfiguration(){
        //Used for serialization purposes
    }

    String getServiceUrl() {
        return this.serviceUrl;
    }

    String getHostname() {
        return this.hostname;
    }

    int getPort() {
        return this.port;
    }

}
