FROM openjdk:17-alpine
WORKDIR '/app'
ENV TZ=Europe/Oslo
COPY server/target/server-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
