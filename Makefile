.PHONY: build
build:
	docker kill test-mongo-instance || true
	docker rm test-mongo-instance || true
	docker kill test-rabbit-instance || true
	docker rm test-rabbit-instance || true
	docker run -d --name "test-mongo-instance" -p 27017:27017 mongo
	docker run -d --name "test-rabbit-instance" -p 5673:5672 rabbitmq:3.6.9
	./gradlew -i -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts build test sonarqube
	docker kill test-mongo-instance
	docker rm test-mongo-instance
	docker kill test-rabbit-instance
	docker rm test-rabbit-instance

.PHONY: clean
clean:
	./gradlew -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts clean

.PHONY: shadow
shadow: clean build
	./gradlew -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts bootRepackage 

.PHONY: docker-build
docker-build: shadow
	for i in `find ./ -maxdepth 1 -type d | egrep -v "(\./$$|build|idea|gradle|\.git|config)"`; \
	do \
		echo $$i && \
		(cd $$i && \
			docker-compose kill && \
			docker-compose rm -f && \
			docker-compose build); \
	done;

