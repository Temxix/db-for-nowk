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

### Сообщения

#### Отправка сообщения
- **URL**: `/api/messages`
- **Метод**: `POST`
- **Тело запроса**:
```json
{
    "content": "Текст сообщения",
    "sender": "Имя отправителя",
    "recipient": "Имя получателя"
}
```

#### Получение сообщений
- **URL**: `/api/messages`
- **Метод**: `GET`
- **Параметры**:
  - `recipient` (опционально) - фильтр по получателю
- **Ответ**: Массив сообщений в формате JSON

### Пользователи

#### Регистрация пользователя
- **URL**: `/api/users/register`
- **Метод**: `POST`
- **Тело запроса**:
```json
{
    "name": "Имя пользователя",
    "publicKey": "Публичный ключ"
}
```

#### Получение списка имен пользователей
- **URL**: `/api/users/names`
- **Метод**: `GET`
- **Ответ**: Массив имен пользователей

#### Приветственное сообщение
- **URL**: `/api/users/welcome`
- **Метод**: `GET`
- **Параметры**:
  - `name` - имя пользователя
- **Ответ**: Приветственное сообщение

## Примеры использования

### Сообщения

#### Отправка сообщения
```bash
curl -X POST http://localhost:8080/api/messages \
-H "Content-Type: application/json" \
-d '{"content": "Привет, мир!", "sender": "Иван", "recipient": "Петр"}'
```

#### Получение всех сообщений
```bash
curl http://localhost:8080/api/messages
```

#### Получение сообщений для конкретного получателя
```bash
curl http://localhost:8080/api/messages?recipient=Петр
```

### Пользователи

#### Регистрация пользователя
```bash
curl -X POST http://localhost:8080/api/users/register \
-H "Content-Type: application/json" \
-d '{"name": "Иван", "publicKey": "abc123"}'
```

#### Получение списка имен
```bash
curl http://localhost:8080/api/users/names
```

#### Получение приветствия
```bash
curl http://localhost:8080/api/users/welcome?name=Иван
```

## Структура проекта

```
src/main/java/com/example/messageserver/
├── MessageServerApplication.java    # Точка входа приложения
├── controller/                      # REST контроллеры
│   ├── MessageController.java
│   └── UserController.java
├── model/                          # Модели данных
│   ├── Message.java
│   └── User.java
└── service/                        # Бизнес-логика
    ├── MessageService.java
    └── UserService.java
```

## Технологии

- Spring Boot 3.2.3
- Spring Web
- Lombok
- Maven
