FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /project
COPY . /project/
RUN mvn clean package
EXPOSE 8080
CMD ["mvn", "mn:run"]