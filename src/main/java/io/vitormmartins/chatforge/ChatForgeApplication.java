package io.vitormmartins.chatforge;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for ChatForge.
 * Refactored to follow Hexagonal Architecture with DDD principles.
 */
@SpringBootApplication(
    exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
    }
)
@OpenAPIDefinition(
    info = @Info(
        title = "ChatForge API",
        version = "1.0",
        description = "API Documentation for ChatForge Application - Real-time Web Chat"
    )
)
@EnableJpaRepositories(basePackages = "io.vitormmartins.chatforge.infrastructure.persistence.user.repository")
@EntityScan(basePackages = "io.vitormmartins.chatforge.infrastructure.persistence.user.entity")
@EnableTransactionManagement
public class ChatForgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatForgeApplication.class, args);
    }
}
