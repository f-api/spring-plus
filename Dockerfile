FROM openjdk:17-jdk-alpine
LABEL authors="durururuk"

COPY build/libs/expert-0.0.1-SNAPSHOT.jar /expert-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/expert-0.0.1-SNAPSHOT.jar"]
