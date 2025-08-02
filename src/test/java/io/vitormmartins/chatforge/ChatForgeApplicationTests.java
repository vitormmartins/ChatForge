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
    "spring.jpa.open-in-view=false",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
})
class ChatForgeApplicationTests {

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
        // with the new hexagonal architecture
    }
}
