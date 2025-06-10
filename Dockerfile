FROM eclipse-temurin:24-jdk AS build

RUN apt-get update && apt-get install -y maven ca-certificates

WORKDIR /app
COPY . .

RUN mvn clean package

FROM eclipse-temurin:24-jdk

RUN apt-get update && apt-get install -y ca-certificates

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-Djavax.net.debug=ssl,handshake", "-jar", "app.jar"]
