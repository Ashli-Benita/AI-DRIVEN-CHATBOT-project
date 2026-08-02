FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/chatbot-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]