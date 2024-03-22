FROM maven:3.9.6-eclipse-temurin-21-alpine as build
WORKDIR /workspace/app
COPY pom.xml .
COPY src src
RUN mvn -Dmaven.test.skip package
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
