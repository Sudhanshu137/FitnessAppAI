# 🏋️‍♂️ Fitness App – Microservices Architecture

A backend microservices project built using **Spring Boot**, **Spring Cloud Gateway**, and **Keycloak** for authentication and authorization.  
The system follows **OAuth2 + JWT**, with centralized security and user synchronization.

---

## 📌 Tech Stack

- Java 17
- Spring Boot
- Spring Cloud Gateway (WebFlux)
- Spring Security (OAuth2 Resource Server)
- Keycloak
- Swagger / OpenAPI
- MySQL / PostgreSQL
- Docker
- Maven

---

---

## 🤖 AI & Intelligence Layer

This project is designed to support **AI-powered fitness intelligence** on top of the core microservices platform.

### AI Capabilities (Current / Planned)

- AI-assisted fitness activity analysis
- Personalized insights based on user behavior
- Integration with external AI models (Gemini)
- Intelligent decision-making using user activity data
- Scalable AI-ready architecture

> The AI layer is intentionally decoupled from authentication and gateway logic to ensure clean separation of concerns.

---

## 🧩 Microservices Overview

| Service | Description |
|------|------------|
| API Gateway | Single entry point, JWT validation, user sync |
| User Service | Stores application-specific user data |
| Other Services | Business logic (activities, etc.) |
| Keycloak | Authentication & Authorization |

---

## 🔐 Authentication & Authorization

- Authentication is handled **entirely by Keycloak**
- Users log in using username & password via Keycloak
- Keycloak issues a **JWT access token**
- API Gateway validates the token
- Roles are extracted from JWT and enforced at Gateway / service level

❌ No passwords are stored in application databases  
❌ No custom login or signup APIs

---

## 👤 User Management Flow

1. User is created in **Keycloak**
2. User logs in and receives JWT
3. Client sends request with `Authorization: Bearer <token>`
4. API Gateway validates JWT
5. Gateway synchronizes user into User Service DB
6. Request is forwarded to downstream services

➡️ One Keycloak user maps to exactly one User Service record.

---

## 🔄 User Synchronization

- User details are extracted from JWT
- User is created in DB only if not present
- Sync happens automatically on first authenticated request

---

## 🌐 API Gateway Responsibilities

- JWT validation
- Role-based authorization
- User synchronization
- Routing to microservices
- Single entry point
- Swagger access via Gateway only

---

## 📖 Swagger / API Documentation

Swagger is enabled in each microservice but accessed **only through API Gateway**.

Example:
