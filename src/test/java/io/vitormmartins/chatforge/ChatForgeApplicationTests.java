package io.vitormmartins.chatforge;

import io.vitormmartins.chatforge.config.TestSecurityConfig;
import io.vitormmartins.chatforge.config.TestJwtConfig;
import io.vitormmartins.chatforge.config.TestMongoConfig;
import io.vitormmartins.chatforge.config.TestSecurityComponentsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test for the main application context.
 * Uses test configurations to avoid issues with real components.
 */
@SpringBootTest
@ActiveProfiles("test")
@Import({
    TestSecurityConfig.class, 
    TestJwtConfig.class, 
    TestMongoConfig.class, 
    TestSecurityComponentsConfig.class
})
@TestPropertySource(properties = {
    "spring.main.allow-bean-definition-overriding=true",
    "spring.jpa.open-in-view=false"
})
class ChatForgeApplicationTests {

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
        // with the new hexagonal architecture
    }
}
