FROM openjdk:17
EXPOSE 8080
ADD target/workshop-api-docker.jar workshop-api-docker.jar
ENTRYPOINT ["java", "-jar","/workshop-api-docker.jar"]