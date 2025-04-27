# 🛡️ DevCircle Auth Service

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.2.4-brightgreen)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blueviolet)
![Swagger](https://img.shields.io/badge/Docs-Swagger_UI-orange)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

A secure, production-grade authentication microservice for the **DevCircle platform**.  
This service handles user registration, login, password hashing, and JWT-based authentication using Spring Boot.

> 🔐 Secure. ☁️ Scalable. 💥 Swagger-documented.

![CI](https://github.com/BailurVikramBhat/auth-service/actions/workflows/maven.yml/badge.svg)

---

## 🧰 Tech Stack

```plaintext
| Layer            | Technology                     |
| ---------------- | ------------------------------ |
| Language         | Java 17                        |
| Framework        | Spring Boot 3.2.4              |
| ORM              | Spring Data JPA (Hibernate)    |
| Database         | PostgreSQL                     |
| Security         | Spring Security + JWT          |
| API Docs         | SpringDoc OpenAPI (Swagger UI) |
| Build Tool       | Maven                          |
| Testing (future) | JUnit + Mockito                |
```

---

## 📁 Project Structure

```plaintext
auth-service/
├── config/             # Security and Swagger configuration
├── controller/         # REST endpoints (e.g., AuthController)
├── dto/                # Data Transfer Objects (RegisterRequest, LoginRequest)
├── exception/          # Custom exceptions + global error handler
├── model/              # JPA entity classes (e.g., User)
├── repository/         # Spring Data JPA repositories (UserRepository)
├── service/            # Business logic (UserService, JwtService)
├── resources/
│   └── application.properties  # Spring Boot configuration
└── AuthServiceApplication.java # Main application entry point
```

---

## 🛠️ Getting Started (Local Setup)

### ✅ Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL installed and running
- An IDE like IntelliJ IDEA or VS Code

---

### 🗃️ Database Setup

1. Start PostgreSQL and create the database manually:

   ```sql
   CREATE DATABASE auth_service_db;
   ```

2. Use your PostgreSQL username and password in `application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/auth_service_db
   spring.datasource.username=your_pg_username
   spring.datasource.password=your_pg_password
   spring.jpa.hibernate.ddl-auto=update
   ```

---

### 🚀 Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

The app will start at: [http://localhost:8081](http://localhost:8081)

---

### 📘 Swagger API Docs

Once the app is running, visit:

[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

---

## 📡 API Usage

All endpoints follow this base path:

```
http://localhost:8081/api/v1/auth
```

---

### 📝 Register User

**Endpoint:** `POST /register`

**Request Body:**

```json
{
  "fullName": "vikram bhat",
  "email": "vikram@dc.com",
  "password": "supersecret"
}
```

**Response:**

```json
{
  "userId": "ad9b930c-25fb-4f04-b8b5-bf1c2737c0b7",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyYW1lc2hAZGRjLmNvbSIsImlhdCI6MTc0NTc2OTMyOCwiZXhwIjoxNzQ1ODU1NzI4fQ.TNKeApsoz2lpM6wTlfdcomc2haijq8KRuo1zA31Su_s",
  "message": "Successfully created the user"
}
```

---

### 🔐 Login User

**Endpoint:** `POST /login`

**Request Body:**

```json
{
  "email": "vikram@dc.com",
  "password": "supersecret"
}
```

**Success Response:**

```json
{
  "uuid": "8451a7fd-a293-4f2a-8a19-b49f3dd4dba7",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJraXNoYW5AZGRjLmNvbSIsImlhdCI6MTc0NTc2ODE0NCwiZXhwIjoxNzQ1ODU0NTQ0fQ.__witbn0vbVRDcRme-IfO2bmWyh8NAzo2QgzRPT_cg8"
}
```

**Invalid Email/Password Response:**

```json
{
  "status": 401,
  "error": "unauthorized",
  "message": "Email id not found.",
  "timestamp": "2025-04-25T00:05:56.9607811"
}
```

---

### 📦 JWT Usage

Once you receive a token from `/login`, use it in the `Authorization` header to access protected routes:

```
Authorization: Bearer <your_jwt_token>
```
