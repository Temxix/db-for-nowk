# NOWK Message Server

Сервер для обмена сообщениями с поддержкой шифрования и MongoDB.

## Описание

NOWK Message Server - это REST API сервер, который предоставляет функционал для:
- Регистрации пользователей
- Обмена зашифрованными сообщениями
- Управления чатами между пользователями
- Безопасного хранения данных в MongoDB

## Технический стек

- Java 24
- Spring Boot 3.4.5
- MongoDB Atlas
- Maven
- RSA шифрование

## Требования

- JDK 24
- Maven 3.8+
- MongoDB Atlas аккаунт
- Доступ к интернету

## Установка и настройка

1. Клонируйте репозиторий:
```bash
git clone [url-репозитория]
cd db-for-nowk
```

2. Создайте аккаунт на [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)

3. Создайте новый кластер в MongoDB Atlas

4. Получите строку подключения к базе данных

5. Настройте `application.properties`:
```properties
spring.data.mongodb.uri=ваша_строка_подключения
spring.data.mongodb.database=NOWK_DB
```

## Запуск

```bash
mvn spring-boot:run
```

Сервер запустится на порту 8080 и будет доступен по адресу `http://localhost:8080`

## API Endpoints

### Пользователи

#### Регистрация пользователя
```
POST /api/users/register
Content-Type: application/json

{
    "name": "имя_пользователя",
    "publicKey": "публичный_ключ_RSA"
}
```

#### Получение списка пользователей
```
GET /api/users/names
```
Возвращает список всех зарегистрированных пользователей.

#### Приветственное сообщение
```
GET /api/users/welcome?name=имя_пользователя
```
Возвращает приветственное сообщение в формате JSON:
```json
{
    "message": "Добро пожаловать!",
    "username": "имя_пользователя"
}
```

#### Получение чатов пользователя
```
GET /api/users/chats/list?username=имя_пользователя
```
Возвращает список всех чатов пользователя. Чат создается автоматически при отправке первого сообщения.

### Сообщения

#### Отправка сообщения
```
POST /api/messages
Content-Type: application/json

{
    "text": "текст_сообщения",
    "username": "отправитель",
    "recipient": "получатель"
}
```
При первой отправке сообщения автоматически создается чат между пользователями.

#### Получение сообщений
```
GET /api/messages?username=имя_пользователя&recipient=имя_получателя
```

## Структура данных

### Пользователь (User)
```json
{
    "id": "string",
    "name": "string",
    "publicKey": "string",
    "chats": [
        {
            "id": "string",
            "recipient": "string",
            "messageIds": ["string"],
            "hasNewMessages": boolean,
            "lastActivity": "datetime"
        }
    ]
}
```

### Сообщение (Message)
```json
{
    "id": "string",
    "text": "string",
    "sender": "string",
    "recipient": "string",
    "timestamp": "datetime"
}
```

## Особенности работы

### Чаты
- Чат создается автоматически при отправке первого сообщения
- Время последней активности (lastActivity) обновляется только при получении новых сообщений
- При получении сообщений чат помечается как прочитанный (hasNewMessages = false)

### Сообщения
- Каждое сообщение сохраняется дважды:
  - Одно для отправителя (sentByMe = true)
  - Одно для получателя (sentByMe = false)
- Сообщения хранятся в зашифрованном виде

## Безопасность

- Все сообщения шифруются с использованием RSA-шифрования
- Каждый пользователь должен предоставить свой публичный RSA-ключ при регистрации
- Сообщения хранятся в зашифрованном виде в MongoDB
- Поддержка CORS для безопасного доступа из браузера

## Примеры использования

### Регистрация пользователя
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "alice",
    "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
  }'
```

### Отправка сообщения
```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Привет!",
    "username": "alice",
    "recipient": "bob"
  }'
```

### Получение сообщений
```bash
curl "http://localhost:8080/api/messages?username=alice&recipient=bob"
```

## Логирование

Сервер использует многоуровневое логирование:
- INFO: основная информация о работе сервера
- DEBUG: детальная информация для отладки
- WARN: предупреждения
- ERROR: ошибки

## Разработка

### Структура проекта
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── messageserver/
│   │               ├── controller/
│   │               ├── service/
│   │               ├── repository/
│   │               ├── model/
│   │               └── dto/
│   └── resources/
│       └── application.properties
└── test/
```

### Тестирование
```bash
mvn test
```

## Лицензия

MIT License
