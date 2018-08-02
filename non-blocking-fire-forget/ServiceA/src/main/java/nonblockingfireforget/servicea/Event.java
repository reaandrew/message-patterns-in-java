package nonblockingfireforget.servicea;

public class Event {
    private String id;
    private String someProperty;
    private String someOtherProperty;

    public Event(String id, String someProperty, String someOtherProperty){
        this.id = id;
        this.someProperty = someProperty;
        this.someOtherProperty = someOtherProperty;
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

        Event event = (Event) o;

        if (id != null ? !id.equals(event.id) : event.id != null) {
            return false;
        }
        if (someProperty != null ? !someProperty.equals(event.someProperty) : event.someProperty != null) {
            return false;
        }
        return someOtherProperty != null ? someOtherProperty.equals(event.someOtherProperty) : event.someOtherProperty == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (someProperty != null ? someProperty.hashCode() : 0);
        result = 31 * result + (someOtherProperty != null ? someOtherProperty.hashCode() : 0);
        return result;
    }

    public String getSomeProperty() {
        return someProperty;
    }

    public String getSomeOtherProperty() {
        return someOtherProperty;
    }
}
