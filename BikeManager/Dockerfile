# Stage 1: Build Phase
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /project
COPY . /project/
RUN echo "\nkafka.enabled=false" >> src/main/resources/application.properties
RUN mvn clean package

# Stage 2: Runtime Phase
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /project/target/*.jar app.jar
RUN sed -i '/kafka.enabled/d' src/main/resources/application.properties || true
EXPOSE 8080
# Run the application with the JAR
CMD ["java", "-jar", "app.jar"]
