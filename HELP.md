# Read Me First
The following was discovered as part of building this project:

* The original package name 'io.vitormmartins.chatforge.spring-chatforge' is invalid and this project uses 'io.vitormmartins.chatforge.spring_chatforge' instead.

# Getting Started

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


# Creating Docker Images for the Project

This guide explains how to create Docker images for MySQL, MongoDB, and RabbitMQ for your project.

## MySQL

To create a Docker container for MySQL, use the following command:

```shell
sudo docker run --name some-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -p 3306:3306 -d mysql:latest
```
This command will:  
- Pull the latest MySQL image from Docker Hub.
- Create a container named some-mysql.
- Set the root password to my-secret-pw.
- Map port 3306 on your host to port 3306 in the container.
- Run the container in detached mode.
- You can verify the container is running with:

```shell
sudo docker container list
```
## MongoDB

To create a Docker container for MongoDB, use the following command:

First, try to connect to MongoDB to ensure it is not already running:

```shell
curl -G localhost:27017
```

If you get a connection refused error, then MongoDB is not running. You can create a Docker container for MongoDB with the following command:

```shell
sudo docker run --name some-mongo -p 27017:27017 -d mongo:latest
```
This command will:
- Pull the latest MongoDB image from Docker Hub.
- Create a container named some-mongo.
- Map port 27017 on your host to port 27017 in the container.
- Run the container in detached mode. 
 
You can verify the container is running with:

```shell
sudo docker container list
```

## RabbitMQ
To create a Docker container for RabbitMQ, use the following command:

```shell
sudo docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 rabbitmq:3
```
This command will:
- Pull the latest RabbitMQ image from Docker Hub.
- Create a container named some-rabbit.
- Map port 5672 on your host to port 5672 in the container.
- Run the container in detached mode.
- You can verify the container is running with:

```shell
sudo docker container list
```

## Summary
By following these steps, you will have Docker containers running MySQL, MongoDB, and RabbitMQ, 
which are essential for your project's development environment.

# Running the Project

## Starting Docker Containers

If your Docker containers for MySQL, MongoDB, and RabbitMQ have been stopped, you can start them again using the following commands:

1. List all containers (including stopped ones) to see their status:
    ```shell
    sudo docker container list -a
    ```

2. Start the RabbitMQ container:
    ```shell
    sudo docker container start some-rabbit
    ```

3. Start the MongoDB container:
    ```shell
    sudo docker container start some-mongo
    ```

4. If MySQL is already running on your host machine, stop the MySQL service to free up the port:
    ```shell
    sudo service mysql stop
    ```

5. Start the MySQL container:
    ```shell
    sudo docker container start some-mysql
    ```

By following these steps, you will have your Docker containers for MySQL, MongoDB, and RabbitMQ running again.
