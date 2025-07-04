package io.vitormmartins.chatforge;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for ChatForge.
 * Refactored to follow Hexagonal Architecture with DDD principles.
 */
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "ChatForge API",
        version = "1.0",
        description = "API Documentation for ChatForge Application - Real-time Web Chat"
    )
)
public class ChatForgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatForgeApplication.class, args);
    }
}
