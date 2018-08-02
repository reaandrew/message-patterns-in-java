package nonblockingrequestresponse.servicea.core;

public class Resource {
    private String id;
    private String someProperty;
    private String someOtherProperty;
    private String subResourceRef;

    public Resource(String id,
                    String someProperty,
                    String someOtherProperty,
                    String subResourceRef){
        this.id = id;
        this.someProperty = someProperty;
        this.someOtherProperty = someOtherProperty;
        this.subResourceRef = subResourceRef;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource resource = (Resource) o;

        if (id != null ? !id.equals(resource.id) : resource.id != null) {
            return false;
        }
        if (getSomeProperty() != null ? !getSomeProperty().equals(resource.getSomeProperty()) : resource.getSomeProperty() != null) {
            return false;
        }
        if (getSomeOtherProperty() != null ? !getSomeOtherProperty().equals(resource.getSomeOtherProperty()) : resource.getSomeOtherProperty() != null) {
            return false;
        }
        return getSubResourceRef() != null ? getSubResourceRef().equals(resource.getSubResourceRef()) : resource.getSubResourceRef() == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (getSomeProperty() != null ? getSomeProperty().hashCode() : 0);
        result = 31 * result + (getSomeOtherProperty() != null ? getSomeOtherProperty().hashCode() : 0);
        result = 31 * result + (getSubResourceRef() != null ? getSubResourceRef().hashCode() : 0);
        return result;
    }

    public String getSomeProperty() {
        return someProperty;
    }

    public String getSomeOtherProperty() {
        return someOtherProperty;
    }

    public String getSubResourceRef() {
        return subResourceRef;
    }
}
