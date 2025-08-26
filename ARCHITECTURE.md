# ChatForge - Hexagonal Architecture Implementation

## Architecture Overview

ChatForge has been refactored to follow **Hexagonal Architecture (Ports and Adapters)** combined with **Domain-Driven Design (DDD)** principles. This architectural approach provides better separation of concerns, testability, and maintainability for a growing webchat application.

## Architecture Layers

### 1. Domain Layer (`domain/`)
**Pure business logic - No dependencies on frameworks**

```
domain/
├── user/
│   ├── model/           # Domain entities and value objects
│   │   ├── User.java    # Main user entity
│   │   ├── UserId.java  # User identifier value object
│   │   ├── Username.java # Username with validation
│   │   ├── Email.java   # Email with validation
│   │   └── Password.java # Password value object
│   ├── repository/      # Repository interfaces (ports)
│   │   └── UserRepository.java
│   └── service/         # Domain services
│       └── UserDomainService.java
```

**Key Principles:**
- Contains core business rules and logic
- Framework independent (no Spring annotations)
- Uses value objects for type safety
- Domain entities encapsulate business behavior

### 2. Application Layer (`application/`)
**Use cases and application services**

```
application/
└── user/
    ├── dto/             # Data transfer objects
    │   ├── RegisterUserCommand.java
    │   ├── AuthenticateUserCommand.java
    │   └── UserDto.java
    └── service/         # Application services and ports
        ├── UserApplicationService.java
        └── PasswordEncoderPort.java
```

**Key Principles:**
- Orchestrates domain objects
- Defines use cases (register user, authenticate user)
- Converts between domain models and DTOs
- Defines ports for infrastructure dependencies

### 3. Infrastructure Layer (`infrastructure/`)
**Framework-specific implementations (adapters)**

```
infrastructure/
├── persistence/
│   └── user/
│       ├── entity/      # JPA entities
│       ├── repository/  # Spring Data repositories
│       ├── mapper/      # Domain ↔ JPA mapping
│       └── adapter/     # Repository implementations
└── security/
    ├── adapter/         # Security adapters
    └── service/         # Security services
```

**Key Principles:**
- Implements domain repository interfaces
- Handles persistence with JPA
- Provides security implementations
- Maps between domain and infrastructure models

### 4. Web Layer (`web/`)
**REST controllers and DTOs**

```
web/
└── controller/
    ├── AuthController.java
    ├── UserController.java
    └── dto/             # Web-specific DTOs
        ├── LoginRequest.java
        └── RegisterRequest.java
```

### 5. Configuration (`config/`)
**Spring configuration classes**

```
config/
├── ApplicationConfig.java  # Domain and application beans
└── SecurityConfig.java     # Security configuration
```

## Benefits of This Architecture

### 1. **Separation of Concerns**
- Business logic is isolated from framework concerns
- Each layer has a clear responsibility
- Easy to understand and maintain

### 2. **Testability**
- Domain logic can be unit tested without Spring
- Application services can be tested with mock repositories
- Infrastructure can be tested separately

### 3. **Technology Independence**
- Can easily switch from JPA to MongoDB
- Can change from Spring Security to another auth framework
- Business logic remains unchanged

### 4. **Scalability for Chat Features**
Perfect foundation for adding:
- **Chat Rooms**: New domain aggregate
- **Messages**: Message entity with rich domain behavior
- **Real-time Features**: WebSocket adapters
- **File Sharing**: File domain with storage adapters
- **Notifications**: Event-driven architecture

### 5. **Microservices Ready**
- Each domain aggregate can become a separate service
- Clear boundaries between contexts
- Easy to extract services when needed

## Usage Examples

### Register a User
```java
// Application Service
RegisterUserCommand command = new RegisterUserCommand("john_doe", "john@example.com", "password123");
UserDto user = userApplicationService.registerUser(command);
```

### Authenticate a User
```java
// Application Service
AuthenticateUserCommand command = new AuthenticateUserCommand("john_doe", "password123");
Optional<UserDto> user = userApplicationService.authenticateUser(command);
```

### Domain Business Rules
```java
// Domain Layer - Username validation
Username username = new Username("john_doe"); // Validates format automatically

// Domain Layer - User creation with business rules
User user = User.create(username, email, password);
```

## Migration from Old Structure

The old monolithic `spring_chatforge` package structure has been replaced with this hexagonal architecture. Key improvements:

1. **Domain Models**: Rich domain entities instead of anemic JPA entities
2. **Value Objects**: Type-safe Username, Email, Password instead of plain strings
3. **Ports and Adapters**: Clear separation between domain and infrastructure
4. **Application Services**: Use case orchestration instead of direct repository usage in controllers

## Future Extensions

This architecture is perfectly suited for a chat application's growth:

### Chat Domain
```
domain/
└── chat/
    ├── model/
    │   ├── ChatRoom.java
    │   ├── Message.java
    │   └── Participant.java
    ├── repository/
    │   ├── ChatRoomRepository.java
    │   └── MessageRepository.java
    └── service/
        └── ChatDomainService.java
```

### Real-time Infrastructure
```
infrastructure/
├── websocket/
│   └── adapter/
│       └── WebSocketMessageAdapter.java
└── messaging/
    └── adapter/
        └── RabbitMQAdapter.java
```

This architecture provides a solid foundation for building a comprehensive, scalable webchat application while maintaining clean code principles and testability.
