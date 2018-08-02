package blockingrequestresponse.servicea;

public class BlockingRequestResponseConfiguration {

    private String hostname;

    private int port;

    private String serviceUrl;

    BlockingRequestResponseConfiguration(){
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
