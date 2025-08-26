package io.vitormmartins.chatforge.config;

import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Configuration for JWT utilities in tests.
 * Provides a fully initialized JwtUtil with a test secret key.
 */
@TestConfiguration
public class TestJwtConfig {
    
    private static final String TEST_SECRET_KEY = "OBslwXQbeJPMIq2bNCmAADfBF36Tda8BhRrXebM8zG2P2ksGSCol9f9bDZpVH6gn";
    
    @Bean
    @Primary
    public JwtUtil jwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKeyString", TEST_SECRET_KEY);
        jwtUtil.init();
        return jwtUtil;
    }
}
