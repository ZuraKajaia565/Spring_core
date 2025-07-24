# Gym CRM - Customer Relationship Management System

A comprehensive gym management system built with Spring Boot that handles trainee and trainer management, training sessions, and workload tracking.

## Features

### Core Functionality
- **User Management**: Registration and authentication for trainees and trainers
- **Training Sessions**: Create, schedule, and manage training sessions
- **Trainer Specializations**: Support for different training types and specializations  
- **Workload Tracking**: Monitor trainer workloads and send notifications
- **Security**: JWT-based authentication with login attempt protection

### Technical Features
- **Microservice Architecture**: Service discovery with Eureka
- **Message Queue**: ActiveMQ for asynchronous communication
- **Circuit Breakers**: Resilience4j for fault tolerance
- **API Documentation**: OpenAPI/Swagger integration
- **Database Support**: MariaDB with H2 for testing
- **Containerization**: Full Docker support with docker-compose

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Gym CRM Main  │    │  Workload Svc   │    │   Discovery     │
│   (Port 8080)   │◄──►│   (Port 8081)   │    │   (Eureka)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │    ActiveMQ     │
                    │  Message Queue  │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │     MariaDB     │
                    │    Database     │
                    └─────────────────┘
```

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.6
- **Language**: Java 17
- **Security**: Spring Security with JWT
- **Database**: 
  - MariaDB (Production)
  - H2 (Testing)
- **ORM**: Spring Data JPA/Hibernate
- **Messaging**: ActiveMQ
- **Documentation**: SpringDoc OpenAPI

### Microservices
- **Service Discovery**: Netflix Eureka
- **HTTP Client**: OpenFeign
- **Circuit Breaker**: Resilience4j
- **Load Balancing**: Spring Cloud LoadBalancer

### Testing
- **Unit Testing**: JUnit 5
- **BDD Testing**: Cucumber
- **Integration Testing**: TestContainers
- **API Testing**: REST Assured
- **Mocking**: WireMock

### DevOps
- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **Build Tool**: Maven
- **Health Monitoring**: Spring Actuator

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Git

## Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Spring_core
```

### 2. Run with Docker Compose
```bash
# Start all services
docker-compose up -d

# Check service health
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

### 3. Access the Application
- **Main API**: http://localhost:8080
- **Workload Service**: http://localhost:8081
- **API Documentation**: http://localhost:8080/swagger-ui.html

## Manual Setup

### Database Setup
```bash
# MariaDB setup (if not using Docker)
mysql -u root -p
CREATE DATABASE dev_db;
CREATE USER 'devuser'@'%' IDENTIFIED BY 'devpass';
GRANT ALL PRIVILEGES ON dev_db.* TO 'devuser'@'%';
```

### Build and Run
```bash
# Build the application
./mvnw clean package

# Run the main application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run Cucumber tests
./mvnw exec:java@run-cucumber
```

## 📚 API Documentation

### Authentication Endpoints
```bash
# Register a trainee
POST /api/trainees
Content-Type: application/json
{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-01-01",
  "address": "123 Main St"
}

# Register a trainer
POST /api/trainers
Content-Type: application/json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "specialization": {
    "id": 1,
    "trainingTypeName": "Cardio"
  }
}

# Login
POST /api/login
Content-Type: application/json
{
  "username": "john.doe",
  "password": "generated_password"
}
```

### Training Management
```bash
# Add training session
POST /training
Authorization: Bearer <jwt_token>
{
  "traineeUsername": "john.doe",
  "trainerUsername": "jane.smith",
  "trainingName": "Morning Cardio",
  "trainingDate": "2024-01-15",
  "trainingDuration": 60
}

# Get training types
GET /trainingTypes
Authorization: Bearer <jwt_token>
```

## Testing

### Run All Tests
```bash
./mvnw test
```

### Run Cucumber BDD Tests
```bash
./mvnw exec:java@run-cucumber
```

### Run Integration Tests
```bash
./mvnw test -Dtest="*IntegrationTest"
```

### Test Reports
- **Surefire Reports**: `target/surefire-reports/`
- **Cucumber Reports**: `target/cucumber-reports/cucumber.html`

## Security Features

### Authentication & Authorization
- **JWT Tokens**: Stateless authentication
- **Role-based Access**: Trainee and Trainer roles
- **Login Protection**: 3 failed attempts = 5-minute lockout
- **Password Security**: BCrypt hashing with 10+ character minimum

### Security Headers
- CORS configuration
- CSRF protection disabled (stateless JWT)
- Session management: STATELESS

## Environment Configuration

### Development Profile
```properties
spring.profiles.active=dev
spring.datasource.url=jdbc:mariadb://localhost:3306/dev_db
spring.activemq.broker-url=tcp://localhost:61616
```

### Docker Environment
Environment variables are configured in `docker-compose.yml`:
- Database connection
- ActiveMQ broker URL
- Spring profiles

## Monitoring & Health Checks

### Health Endpoints
- **Main Service**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Info**: `/actuator/info`

### Message Queue Monitoring
ActiveMQ console available at http://localhost:8161/admin

## Message Flow

1. **Training Creation**: Main app → ActiveMQ → Workload Service
2. **Workload Updates**: Async processing via message queue
3. **Circuit Breaker**: Fallback mechanisms for service failures

### Logs
```bash
# View application logs
docker-compose logs gymcrm-main

# View workload service logs  
docker-compose logs gymcrm-report
```

## Project Structure

```
src/
├── main/
│   ├── java/com/zura/gymCRM/
│   │   ├── controller/          # REST controllers
│   │   ├── entities/            # JPA entities
│   │   ├── facade/              # Business facade layer
│   │   ├── security/            # Security configuration
│   │   ├── messaging/           # ActiveMQ messaging
│   │   ├── client/              # Feign clients
│   │   └── dto/                 # Data transfer objects
│   └── resources/
│       └── application.yml      # Configuration
└── test/
    ├── java/                    # Unit tests
    └── resources/features/      # Cucumber feature files
```


For questions and support:
- Create an issue in the repository
- Review API documentation at `/swagger-ui.html` 