FROM java:8

RUN apt-get update
RUN apt-get install -y netcat

ADD config/config.json config.json
ADD build/libs/ServiceA.jar ServiceA.jar

ENV config=config.json

CMD ["java", "-jar", "ServiceA.jar"]
