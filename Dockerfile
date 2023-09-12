FROM openjdk:17
EXPOSE 8080
RUN mkdir /app

COPY build/libs/*.jar /app/test-docker.jar

ENTRYPOINT ["java", "-jar","/test-docker.jar"]
