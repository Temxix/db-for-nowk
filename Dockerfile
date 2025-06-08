# Используем образ с Maven и JDK
FROM maven:3.9.6-eclipse-temurin-17

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем всё в контейнер
COPY . .

# Открываем порт, на котором работает Spring Boot (по умолчанию 8080)
EXPOSE 8080

# Запускаем приложение
CMD ["mvn", "spring-boot:run"]