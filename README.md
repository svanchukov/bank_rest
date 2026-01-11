Bank REST API — Система управления картами и переводами


Основные возможности:
Безопасность (JWT): Авторизация через JSON Web Token. Доступ к эндпоинтам защищен ролями (USER, ADMIN).
Управление картами: Просмотр списка своих карт, проверка баланса и запрос на блокировку.
Денежные переводы: Переводы между картами с проверкой прав владения и достаточности средств.
Пагинация: Список карт возвращается постранично для оптимизации нагрузки.
Администрирование: Специальные эндпоинты для администраторов (просмотр всех пользователей, принудительная блокировка, управление балансом).
Swagger/OpenAPI: Интерактивная документация всех API методов.

Советую запускать из любого компилятора, так будет гораздо удобнее, чем через docker.
Советую подключить базу данных, к примеру, dbeaver или postgres, так как будут видные сразу изменения, команды для создания баз данных:
-- 1. Таблица пользователей
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    fio VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_status VARCHAR(50) DEFAULT 'ACTIVE',
    role VARCHAR(50) DEFAULT 'USER'
);

-- 2. Таблица карт
CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    card_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(19, 2) DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    expiry_date TIMESTAMP NOT NULL,
    owner_id BIGINT NOT NULL,
    CONSTRAINT fk_card_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

После создания баз данных, нужно:
1. Создание админа запросом sql, если он автоамтически не создался (я прописал автоматическое создание, но все же):
INSERT INTO users (email, fio, password, user_status, role)
VALUES ('admin@bank.com', 'Админов Админ Админович', '$2a$10$v7m6.z7yF/1nOqXmXoR4p.uPZ6Xh5N2T7Wp8Y6Xh5N2T7Wp8Y6Xh5', 'ACTIVE', 'ADMIN');
 или VALUES ('admin@bank.com', 'Админов Админ Админович', 'admin@123', 'ACTIVE', 'ADMIN');
3. Тестирование в postman, по адресу http://localhost:8080/auth/login -> body -> { "email": "admin@bank.com, "password": "admin123"
Фото пример: https://github.com/user-attachments/assets/cecab40a-8053-47b1-be6b-b95ffd53a925
4. Выдается токен jwt, его нужно вставить в Authorization -> Bearer Token 
Фото пример: https://github.com/user-attachments/assets/57af10c2-4f08-463f-b11e-76fe3cef0215"
5. После этого можно тестировать остальные сервисы админа, создание карт, проверка:
Карты:
GET /users/{userId}/cards — Получить список всех карт пользователя (с пагинацией).
GET /users/{userId}/cards/{cardId} — Получить детальную информацию по конкретной карте.
GET /users/{userId}/cards/{cardId}/balance — Проверить только баланс конкретной карты.

Операции:
POST /users/{userId}/transfer — Перевод денег. Принимает ID карты отправителя, номер карты получателя и сумму.
PATCH /users/{userId}/cards/{cardId}/block — Запрос на блокировку своей карты.

Админские методы (Только роль ADMIN)
Обычно выносятся в отдельный блок /admin.
Управление пользователями:
GET /admin/users — Список всех пользователей системы.
GET /admin/users/{userId} — Просмотр профиля любого пользователя.
PATCH /admin/users/{userId}/status — Изменение статуса пользователя (например, BLOCK или ACTIVE).

Управление картами и деньгами:
GET /admin/cards — Список всех выпущенных карт в банке.
POST /admin/cards — Выпуск (создание) новой карты для пользователя.
PATCH /admin/cards/{cardId}/balance — Начисление или списание средств (корректировка баланса администратором).
DELETE /admin/cards/{cardId} — Полное удаление карты из системы.

5. Документация (Swagger)
Фото пример: http://localhost:8080/swagger-ui/index.html — Визуальный интерфейс Swagger для тестирования всех вышеперечисленных методов.
Советую сгенерировать токен в postman для удобства и вставить его в значок замка
Фото пример: https://github.com/user-attachments/assets/6e1b4d71-177d-4bbf-b678-d3815c3e7566
Фото пример: https://github.com/user-attachments/assets/0488f8d7-946d-472a-bfd2-447b2925fdf4



