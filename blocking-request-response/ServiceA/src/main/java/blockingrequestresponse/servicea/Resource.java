package blockingrequestresponse.servicea;

public class Resource {
    private String id;
    private String someProperty;
    private String someOtherProperty;
    private String subResourceRef;

    Resource(String id,
             String someProperty,
             String someOtherProperty,
             String subResourceRef){
        this.id = id;
        this.someProperty = someProperty;
        this.someOtherProperty = someOtherProperty;
        this.subResourceRef = subResourceRef;
    }

    String getId() {
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
        if (someProperty != null ? !someProperty.equals(resource.someProperty) : resource.someProperty != null) {
            return false;
        }
        if (someOtherProperty != null ? !someOtherProperty.equals(resource.someOtherProperty) : resource.someOtherProperty != null) {
            return false;
        }
        return subResourceRef != null ? subResourceRef.equals(resource.subResourceRef) : resource.subResourceRef == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (someProperty != null ? someProperty.hashCode() : 0);
        result = 31 * result + (someOtherProperty != null ? someOtherProperty.hashCode() : 0);
        result = 31 * result + (subResourceRef != null ? subResourceRef.hashCode() : 0);
        return result;
    }
}
