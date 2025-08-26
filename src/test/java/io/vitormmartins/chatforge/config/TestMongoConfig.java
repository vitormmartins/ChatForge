package io.vitormmartins.chatforge.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Configuration for mocking MongoDB dependencies in tests.
 * This allows tests to run without an actual MongoDB connection.
 */
@TestConfiguration
public class TestMongoConfig {
    
    @Bean
    @Primary
    public MongoTemplate mongoTemplate() {
        return Mockito.mock(MongoTemplate.class);
    }
}
