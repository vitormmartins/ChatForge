package io.vitormmartins.chatforge.spring_chatforge;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//TODO: Create a README.md file with a description of the project
//TODO: Add a license file

/**
 * The ChatForgeApplication class serves as the entry point for the ChatForge application.
 * It is annotated with Spring Boot's @SpringBootApplication, which encapsulates the
 * configuration, component scanning, and autoconfiguration features.
 * <p>
 * Additionally, this class is annotated with @OpenAPIDefinition to provide metadata
 * for the OpenAPI documentation of the ChatForge application, including the API's title,
 * version, and description.
 */
@SpringBootApplication
@OpenAPIDefinition(
				info = @Info(
								title = "ChatForge API",
								version = "1.0",
								description = "API Documentation for ChatForge Application"
				)
)
public class ChatForgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatForgeApplication.class, args);
	}

}
