package io.vitormmartins.chatforge;

import io.vitormmartins.chatforge.application.user.dto.UserDto;
import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.config.TestSecurityConfig;
import io.vitormmartins.chatforge.config.TestJwtConfig;
import io.vitormmartins.chatforge.config.TestMongoConfig;
import io.vitormmartins.chatforge.config.TestSecurityComponentsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
        "spring.flyway.enabled=false",
        "spring.mongodb.embedded.version=4.4.5",
        "management.health.rabbit.enabled=false",
        "management.health.mongo.enabled=false"
})
class ChatForgeApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserApplicationService userApplicationService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private final String testUsername = "Capivara";
    private final String testPassword = "aravipac";
    private final String testEmail = "capivara@example.com";
    private final long testUserId = 1L;
    private final LocalDateTime testCreatedAt = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Mock authentication for tests that need it
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testUsername,
                testPassword
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
    }

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() throws Exception {
        // Arrange
        UserDto userDto = new UserDto(testUserId, testUsername, testEmail, testCreatedAt);
        when(userApplicationService.findUserByUsername(testUsername))
                .thenReturn(Optional.of(userDto));

        // Act & Assert
        mockMvc.perform(get("/v1/api/users/{username}", testUsername)
                        .with(user(testUsername).password(testPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUsername))
                .andExpect(jsonPath("$.email").value(testEmail));
    }

    @Test
    void getUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        when(userApplicationService.findUserByUsername(testUsername))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/v1/api/users/{username}", testUsername)
                        .with(user(testUsername).password(testPassword)))
                .andExpect(status().isNotFound());
    }

    @Test
    void login_ShouldReturnJwtToken_WhenCredentialsAreValid() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUsername, testPassword));
        when(userApplicationService.authenticateUser(any())).thenReturn(Optional.of(
                new UserDto(testUserId, testUsername, testEmail, testCreatedAt)
        ));

        // Act & Assert
        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "username": "%s",
                        "password": "%s"
                    }
                    """.formatted(testUsername, testPassword)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @Test
    void loginWithCookie_ShouldSetAuthCookie_WhenCredentialsAreValid() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(testUsername, testPassword));
        when(userApplicationService.authenticateUser(any())).thenReturn(Optional.of(
                new UserDto(testUserId, testUsername, testEmail, testCreatedAt)
        ));

        // Act & Assert
        mockMvc.perform(post("/v1/auth/login-cookie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "username": "%s",
                        "password": "%s"
                    }
                    """.formatted(testUsername, testPassword)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("auth_token"))
                .andExpect(content().string("Authentication successful"));
    }

    @Test
    void registerUser_ShouldReturnUserDto_WhenRegistrationIsSuccessful() throws Exception {
        // Arrange
        UserDto expectedUser = new UserDto(testUserId, testUsername, testEmail, testCreatedAt);

        when(userApplicationService.registerUser(any())).thenReturn(expectedUser);

        // Act & Assert
        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "username": "%s",
                        "email": "%s",
                        "password": "%s"
                    }
                    """.formatted(testUsername, testEmail, testPassword)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUsername))
                .andExpect(jsonPath("$.email").value(testEmail));
    }

    @Test
    void checkUsernameAvailability_ShouldReturnBoolean() throws Exception {
        // Arrange
        when(userApplicationService.isUsernameAvailable(testUsername)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/v1/api/users/check-username/{username}", testUsername)
                        .with(user(testUsername).password(testPassword)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void actuatorHealth_ShouldReturnUpStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}