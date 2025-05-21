# Message Server

Сервер для обмена зашифрованными сообщениями с использованием MongoDB и Spring Boot.

## Требования

- Java 24
- Maven
- MongoDB Atlas (облачная база данных)

## Конфигурация

1. Создайте аккаунт на [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Создайте новый кластер
3. Получите строку подключения
4. Настройте `application.properties`:
```properties
spring.data.mongodb.uri=ваша_строка_подключения
spring.data.mongodb.database=NOWK_DB
```

## Запуск

```bash
mvn spring-boot:run
```

Сервер запустится на порту 8080.

## API Endpoints

### Пользователи

#### Регистрация пользователя
```
POST /api/users/register
Content-Type: application/json

{
    "name": "имя_пользователя",
    "publicKey": "публичный_ключ"
}
```

#### Получение списка пользователей
```
GET /api/users/names
```

#### Получение приветственного сообщения
```
GET /api/users/welcome?name=имя_пользователя
```
Возвращает зашифрованное приветственное сообщение, используя публичный ключ пользователя.

### Сообщения

#### Отправка сообщения
```
POST /api/messages
Content-Type: application/json

{
    "content": "текст_сообщения",
    "sender": "отправитель",
    "recipient": "получатель"
}
```

#### Получение сообщений пользователя
```
GET /api/messages?username=имя_пользователя&recipient=имя_получателя
```
Возвращает список сообщений между двумя пользователями:
- Сообщения, где `username` является отправителем, а `recipient` - получателем
- Сообщения, где `recipient` является отправителем, а `username` - получателем

Для каждого сообщения указывается флаг `isMine`, показывающий, является ли пользователь `username` отправителем.

Параметры:
- `username` (обязательный) - имя пользователя, чьи сообщения нужно получить
- `recipient` (обязательный) - имя второго пользователя для получения переписки

## Примеры использования

### Регистрация пользователя
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"test","publicKey":"your_public_key"}'
```

### Получение сообщений
```bash
curl http://localhost:8080/api/messages?username=test&recipient=user2
```

### Отправка сообщения
```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"content":"Привет!","sender":"user1","recipient":"user2"}'
```

## Безопасность

- Все сообщения шифруются с использованием RSA-шифрования
- Каждый пользователь имеет свой публичный ключ
- Сообщения хранятся в зашифрованном виде в MongoDB
- Строка подключения к базе данных хранится в переменных окружения

## Технологии

- Spring Boot 3.4.5
- MongoDB
- Spring Data MongoDB
- Lombok
- Java 24
