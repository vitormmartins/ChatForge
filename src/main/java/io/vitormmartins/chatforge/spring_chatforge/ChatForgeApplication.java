//TODO: Create a README.md file with a description of the project
//TODO: Add a license file
//TODO: Add a description of the project here
package io.vitormmartins.chatforge.spring_chatforge;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
