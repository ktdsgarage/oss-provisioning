FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY workflow-manager/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]