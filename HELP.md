# Read Me First

The following was discovered as part of building this project:

* The original package name 'io.vitormmartins.chatforge.spring-chatforge' is invalid and this project uses 'io.vitormmartins.chatforge.spring_chatforge' instead.

## Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.2/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.2/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.2/reference/web/servlet.html)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/3.4.2/reference/actuator/index.html)
* [Spring Session](https://docs.spring.io/spring-session/reference/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.2/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/3.4.2/reference/data/nosql.html#data.nosql.mongodb)
* [WebSocket](https://docs.spring.io/spring-boot/3.4.2/reference/messaging/websockets.html)
* [Spring for RabbitMQ](https://docs.spring.io/spring-boot/3.4.2/reference/messaging/amqp.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.4.2/reference/using/devtools.html)
* [Thymeleaf](https://docs.spring.io/spring-boot/3.4.2/reference/web/servlet.html#web.servlet.spring-mvc.template-engines)
* [Validation](https://docs.spring.io/spring-boot/3.4.2/reference/io/validation.html)
* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/3.4.2/reference/web/spring-security.html#web.security.oauth2.server)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.2/reference/web/spring-security.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)
* [Using WebSocket to build an interactive web application](https://spring.io/guides/gs/messaging-stomp-websocket/)
* [Messaging with RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

## Running the Application

This guide explains how to run the ChatForge application using either Docker Compose (recommended), manually setting up individual containers, or running the application directly.

### Building the Application

Before running the application with any method, you need to build it:

```shell
# Navigate to the project directory
cd /path/to/spring-chatforge

# Build the application with Maven
./mvnw clean package
```

This will create a JAR file in the `target` directory, typically named something like `spring-chatforge-0.0.1-SNAPSHOT.jar`.

### Option 1: Running the Application Directly

To run the application directly on your host machine:

```shell
# Run the JAR file
java -jar target/spring-chatforge-0.0.1-SNAPSHOT.jar
```

You'll need to ensure that MySQL, MongoDB, and RabbitMQ are available at the addresses configured in your `application.properties` file.

### Option 2: Using Docker Compose (Recommended)

Docker Compose offers a streamlined approach to manage all the services required for this application with a single command.

#### Creating a docker-compose.yml File

Create a file named `docker-compose.yml` in the root directory of your project with the following content:

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - mongodb
      - rabbitmq
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/chatforge?createDatabaseIfNotExist=true
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=my-secret-pw
      - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/chatforge
      - SPRING_RABBITMQ_HOST=rabbitmq

  mysql:
    image: mysql:8.3
    environment:
      - MYSQL_ROOT_PASSWORD=my-secret-pw
      - MYSQL_DATABASE=chatforge
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    volumes:
      - mongodb-data:/data/db

  rabbitmq:
    image: rabbitmq:3
    ports:
      - "5672:5672"
      - "15672:15672"

volumes:
  mysql-data:
  mongodb-data:
```

#### Building a Dockerfile for the Application

Create a `Dockerfile` in the root of your project:

```dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Building and Running with Docker Compose

1. Build the application with Maven:

   ```shell
   ./mvnw clean package -DskipTests
   ```

2. To build and start all services:

   ```shell
   docker-compose up --build
   ```

   This command will:
   - Build the application image using the Dockerfile
   - Pull necessary images for MySQL, MongoDB, and RabbitMQ
   - Start all services with the correct configuration
   - Link the services together

3. To run in detached mode (background):

   ```shell
   docker-compose up -d
   ```

4. To stop all services:

   ```shell
   docker-compose down
   ```

### Option 3: Manual Setup of Individual Containers

If you prefer more control over individual services, you can set up each container manually.

#### Creating Docker Images for the Project

##### MySQL

```shell
sudo docker run --name some-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -p 3306:3306 -d mysql:8.3
```

##### MongoDB

First, verify MongoDB is not already running:

```shell
curl -G localhost:27017
```

If you get a connection refused error, create a MongoDB container:

```shell
sudo docker run --name some-mongo -p 27017:27017 -d mongo:latest
```

##### RabbitMQ

```shell
sudo docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 rabbitmq:3
```

#### Starting Existing Docker Containers

If your containers have been stopped, restart them:

1. List all containers:

    ```shell
    sudo docker container list -a
    ```

2. Start the containers:

    ```shell
    sudo docker container start some-rabbit
    sudo docker container start some-mongo
    ```

3. For MySQL, if it's running on your host machine:

    ```shell
    sudo service mysql stop
    sudo docker container start some-mysql
    ```

## Choosing Between Running Methods

### Running Directly (No Docker)
- Simplest for local development with minimal setup
- Requires manual installation of all dependencies (MySQL, MongoDB, RabbitMQ)
- Good for environments where Docker cannot be used
- Allows for direct debugging of the application

### Docker Compose Advantages
- Simpler setup with a single command
- Services are automatically linked together
- Environment configuration is centralized
- Easier to start/stop the entire environment
- More reproducible development environment

### Manual Container Setup Advantages
- More control over individual services
- Useful for understanding how each component works
- Better for troubleshooting specific services
- Can be used alongside a locally running application

For most development purposes, Docker Compose is recommended as it simplifies the process and ensures consistency across environments. For production deployments, consider using container orchestration platforms like Kubernetes.
