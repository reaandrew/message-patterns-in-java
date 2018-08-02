package nonblockingfireforget.servicea;

class PublishEventDto {
    private String someProperty;
    private String someOtherProperty;

    PublishEventDto(){

    }

    PublishEventDto(String someProperty, String someOtherProperty){
        this.someProperty = someProperty;
        this.someOtherProperty = someOtherProperty;
    }

    Event createEvent(String id){
        return new Event(id, this.someProperty, this.someOtherProperty);
    }

}
