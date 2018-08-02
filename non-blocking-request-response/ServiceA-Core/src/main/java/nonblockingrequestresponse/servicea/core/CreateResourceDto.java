package nonblockingrequestresponse.servicea.core;


public class CreateResourceDto {
    private String id;
    private String someProperty;
    private String someOtherProperty;

    public String getSomeProperty() {
        return someProperty;
    }

    public String getSomeOtherProperty() {
        return someOtherProperty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSomeProperty(String someProperty) {
        this.someProperty = someProperty;
    }

    public void setSomeOtherProperty(String someOtherProperty) {
        this.someOtherProperty = someOtherProperty;
    }
}
