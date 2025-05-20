# Message Server

Простой сервер сообщений, реализованный на Spring Boot.

## Требования

- Java 17 или выше
- Maven 3.6 или выше

## Установка

1. Клонируйте репозиторий:
```bash
git clone [URL репозитория]
```

2. Перейдите в директорию проекта:
```bash
cd message-server
```

3. Соберите проект с помощью Maven:
```bash
mvn clean install
```

## Запуск приложения

Запустите приложение с помощью команды:
```bash
mvn spring-boot:run
```

Сервер будет запущен на порту 8080.

## API Endpoints

### Отправка сообщения
- **URL**: `/api/messages`
- **Метод**: `POST`
- **Тело запроса**:
```json
{
    "content": "Текст сообщения",
    "sender": "Имя отправителя"
}
```

### Получение всех сообщений
- **URL**: `/api/messages`
- **Метод**: `GET`
- **Ответ**: Массив сообщений в формате JSON

## Примеры использования

### Отправка сообщения с помощью curl
```bash
curl -X POST http://localhost:8080/api/messages \
-H "Content-Type: application/json" \
-d '{"content": "Привет, мир!", "sender": "Иван"}'
```

### Получение всех сообщений
```bash
curl http://localhost:8080/api/messages
```

## Структура проекта

```
src/main/java/com/example/messageserver/
├── MessageServerApplication.java    # Точка входа приложения
├── controller/                      # REST контроллеры
│   └── MessageController.java
├── model/                          # Модели данных
│   └── Message.java
└── service/                        # Бизнес-логика
    └── MessageService.java
```

## Технологии

- Spring Boot 3.2.3
- Spring Web
- Lombok
- Maven

## Лицензия

Этот проект распространяется под лицензией MIT. 