FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine
WORKDIR /userapp
COPY ./target/spring-data-cache-1.0-SNAPSHOT.jar user-app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} ${JAVA_AGENT_OPTS} -jar user-app.jar"]
