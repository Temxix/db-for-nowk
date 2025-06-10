FROM eclipse-temurin:24-jdk AS build

# Устанавливаем Maven
RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY . .

# Сборка проекта
RUN mvn clean package

# Второй этап — облегчённый runtime
FROM eclipse-temurin:24-jdk

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]