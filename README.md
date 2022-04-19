# Сервер терминала
## Старт

Для запуска и развертывания приложения выполните следющие действия:
 - В файл `envinronment/database.env` впишите свои желаемые креденшиалы и весь энвайронмент для базы данных (она будет развернута в докере рядом с приложением)
 - Запустите приложение командой `sudo docker-compose up --build`
## Примечания
 
К сожалению не было подключено описание OpenAPI, т.к. приложение написано на ktor 2, а большинство библиотек пока не перешло на эту версию ктора
(нестыковки при подключении плагинов). Была найдена хорошая библиотека, которая прикручивает swagger ui и создает документацию без изменения кода приложения, 
но необходимо дождаться её миграции на новую версию ктора (ишью под это уже заведен). 

Сама библиотека: [https://github.com/bkbnio/kompendium](https://github.com/bkbnio/kompendium)

## Модули

- Авторизация
- Терминальные функции

## Авторизация

Авторизация

```json
Request:
POST api/v1/auth/signin
body = {
  "Login": "login"
  "Password": "password"
}
Response: {
  "Status": "OK",
  "Message": {
    "JWT": "jwt-token",
    "Location": "your current location"
  }
} 
```

Обратите внимание, в этой и во все следующих моделях field naming policy = UpperCamelCase

Регистрация

```json
Request:
POST api/v1/auth/signup
body = {
  "Login": "login",
  "Password": "password"
}
Response: {
  "Status": "OK",
  "Message": "Registration done"
} 
```

Ошибки

```json
{
  "Status": "Error",
  "Message": "Bad credentials",
} 
```

## Терминал

Авторизация выдает JWT-токен авторизации, а так же начальный location. 
Этот токен необходимо передавать с каждым запросом в этой секции в заголовке.
Location тоже необходимо хранить на клиенте и передавать на сервер в теле запроса.
Authorization как Bearer токен.

ls - посмотреть содержимое папки

```json
Request:
POST api/v1/terminal/ls
body = {
  "BasePath":"base path from where we watch",
  "Location" : "location we shall ls"
}
Response: {
  "Status": "OK",
  "Message": {
    "response": [
      "examples", "of", "directory", "content"
    ]
  }
}

Error: {
  "Status": "Bad Request",
  "Message": "Problems with location to ls",
  "Code": {
    "Value":"400",
    "Status": "Bad Request"
  }
}
``` 

cd - поменять местоположение

```json
Request:
POST api/v1/terminal/cd
body = {
  "BasePath":"base path from where we go",
  "Location" : "location we shall cd to"
}
Response: {
  "Status": "OK",
  "Message": "/you/new/location/"
}

Error: {
  "Status": "Bad Request",
  "Message": "Wrong location to cd",
}
```

who - посмотреть залогининых пользователей и их локейшоны

```json
Request:
GET api/v1/terminal/who
Response: {
  "Status": "OK",
  "Message": {
    "Response": ["user1", "user2", "userN"]
  }
}
```

kill - привилегированная команда, админ может завершить сеанс другого пользователя

```json
Request:
POST api/v1/terminal/kill
body = {
  "UserToKill": "userName"
}
Response: {
  "Status": "OK",
  "Message": "userName was killed"
}

Error: {
  "Status": "Error",
  "Message": "You have not enough rights",
}
```

logout - завершить сессию

```json
Request:
GET api/v1/terminal/logout
Response: {
  "Status": "OK",
  "Message": "You was killed (logout successful)"
}
```

Ошибки общие:

Не был передан токен: HttpStatusCode.Forbidden
```json
{
  "Status": "Error",
  "Message": "No token, please signIn"
} 
```

Сессия юзера была завершена админом: HttpStatusCode.Unauthorized
```json
{
  "Status": "Logout",
  "Message": "Your session was destroyed"
} 
```