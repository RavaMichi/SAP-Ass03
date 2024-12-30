FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /project
COPY . /project/
RUN mvn dependency:resolve
RUN mvn clean verify
EXPOSE 8080
CMD ["mvn", "mn:run"]
