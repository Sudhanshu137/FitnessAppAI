# 🏋️‍♂️ Fitness App – AI Powered Microservices Architecture

A fully containerized backend microservices project built using **Spring Boot**, **Spring Cloud Gateway**, and **Keycloak** for authentication and authorization.

The system follows **OAuth2 + JWT**, with centralized security, service discovery, configuration management, event-driven communication, and AI-powered recommendations.

---

# 📌 Tech Stack

- Java 17  
- Spring Boot 3  
- Spring Cloud Gateway (WebFlux)  
- Spring Cloud Config Server  
- Netflix Eureka (Service Discovery)  
- Spring Security (OAuth2 Resource Server)  
- Keycloak  
- RabbitMQ  
- MySQL  
- MongoDB  
- Google Gemini API  
- Docker & Docker Compose  
- Maven  

---

# 🏗 Architecture Overview

Client  
↓  
API Gateway  
↓  
-----------------------------------------  
User Service (MySQL)  
Activity Service (MongoDB)  
AI Service (MongoDB + Gemini API)  
-----------------------------------------  
↓  
RabbitMQ (Event Communication)  
↓  
Databases  

Core Infrastructure:

- Eureka Server → Service Discovery  
- Config Server → Centralized Configuration  
- Keycloak → Authentication & Authorization  

---

# 🤖 AI & Intelligence Layer

This project integrates **Google Gemini AI** for generating fitness recommendations.

### AI Capabilities

- AI-assisted activity analysis  
- Personalized fitness recommendations  
- Intelligent suggestion generation  
- Event-driven recommendation processing  
- Scalable AI-ready microservice design  

The AI layer is fully decoupled from authentication and routing logic to maintain clean microservice boundaries.

---

# 🧩 Microservices Overview

| Service | Description |
|----------|------------|
| API Gateway | Single entry point, JWT validation, routing |
| User Service | Stores application-specific user data (MySQL) |
| Activity Service | Stores user activities (MongoDB) |
| AI Service | Generates recommendations using Gemini |
| Config Server | Centralized configuration |
| Eureka Server | Service discovery |
| Keycloak | Authentication & Authorization |
| RabbitMQ | Event-driven communication |

---

# 🔐 Authentication & Authorization

- Authentication is handled entirely by Keycloak  
- Users log in using username & password  
- Keycloak issues a JWT access token  
- API Gateway validates the token  
- Roles are extracted from JWT and enforced at gateway level  

---

# 👤 User Management Flow

1. User is created in Keycloak  
2. User logs in and receives JWT  
3. Client sends request with:

   Authorization: Bearer <token>

4. API Gateway validates JWT  
5. Gateway synchronizes user into User Service DB (if not exists)  
6. Request is forwarded to downstream services  

One Keycloak user maps to exactly one User Service record.

---

# 🔄 User Synchronization

- User details are extracted from JWT  
- If user not present in MySQL → created automatically  
- Sync happens on first authenticated request  

---

# 🌐 API Gateway Responsibilities

- JWT validation  
- Role-based authorization  
- User synchronization  
- Load-balanced routing  
- Single entry point for all APIs  

---

# 🐳 Dockerized Setup (One Command Run)

This project is fully dockerized.

### Requirements

- Docker Desktop installed  
- Git installed  

No need to install:

- MySQL  
- MongoDB  
- RabbitMQ  
- Keycloak  

Everything runs inside containers.

---

# 🚀 How To Run The Project

---

## 1️⃣ Clone Repository

```bash
git clone https://github.com/Sudhanshu137/FitnessAppAI.git
cd FitnessAppAI/FitnessMicroservices
```

---

## 2️⃣ Build JAR Files

Run inside each microservice folder:

```bash
mvn clean package -DskipTests
```

---

## 3️⃣ Start All Services

```bash
docker compose up --build
```

---

## 4️⃣ Access Services

| Service | URL |
|----------|------|
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| Keycloak | http://localhost:8181 |
| RabbitMQ UI | http://localhost:15672 |

---

# 🔑 Keycloak Setup

Open:

http://localhost:8181

Login credentials:

- Username: admin  
- Password: admin  

Create:

- Realm: fitness-oauth2  
- Client: fitness-client  
- Create test users  

---

# 🧪 Testing APIs (Postman)

The repository includes a Postman collection.

### Steps:

1. Open Postman  
2. Click Import  
3. Import collection from `/postman` folder  
4. Generate access token from Keycloak  
5. Add token as Bearer token in requests  

---

# 💾 Databases

### MySQL
- Used by User Service  
- Database: fitness_user_db  
- Auto-created via Docker  

### MongoDB
- Used by:
  - Activity Service  
  - AI Service  
- Databases:
  - activitymicroservice  
  - fitnessrecommendation  

---

# 📡 Event Driven Communication

RabbitMQ Configuration:

Exchange:
fitness.exchange  

Queue:
activity.queue  

Routing Key:
activity.tracking  

Activity events are published and consumed by AI Service.

---

# ⚙ Profiles

| Profile | Purpose |
|----------|---------|
| local | Run manually without Docker |
| docker | Used inside containers |

Docker automatically activates docker profile.

---

# 🎯 What This Project Demonstrates

- Secure Microservices Architecture  
- OAuth2 + JWT Authentication  
- Service Discovery (Eureka)  
- Centralized Configuration  
- API Gateway Pattern  
- Event-driven communication (RabbitMQ)  
- AI Integration (Gemini API)  
- Docker-based full environment orchestration  

---

# 👨‍💻 Author

Sudhanshu Kumar  
B.Tech CSE – Backend & Microservices Developer  
