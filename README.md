# LaundryBookingService

Сервис бронирования стиральных машин. Пользователь бронирует свободный слот, администратор подтверждает или отклоняет заявку. Проект учебный: и для изучения самого Spring, и для практики тестирования бэкенда.

## Стек

- Java 21, Spring Boot 4.1
- PostgreSQL, Flyway
- Gradle, многомодульная сборка
- Spring Security, JWT
- JUnit 5, Mockito, Testcontainers

## Модули

Построен по принципу портов и адаптеров.

- `core`: домен и бизнес-логика.
- `persistence`: репозитории на JPA, миграции Flyway.
- `rest-app`: REST API, JWT, запускаемый модуль.
- `web-app`: веб-интерфейс на Thymeleaf, пока без кода.

## Функциональность

- Регистрация и вход по паролю и через Telegram.
- JWT с refresh-токенами.
- Бронирование слота с защитой от двойной брони.
- Блокировка слотов.
- Подтверждение и отклонение броней администратором.

API под префиксом `/api/v1`.

## Запуск

Нужны JDK 21 и Docker. База поднимается автоматически через docker-compose.

```
./gradlew :rest-app:bootRun
```

API на `http://localhost:8080/api/v1`.

## Тесты

По уровням пирамиды:

- юнит (`core`) на Mockito,
- интеграционные (`persistence`) на Testcontainers,
- веб-слой (`rest-app`, `@WebMvcTest`),
- сквозные (`rest-app`, `@SpringBootTest`).

```
./gradlew test
```

Интеграционным и сквозным тестам нужен Docker.
