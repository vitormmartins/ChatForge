package io.vitormmartins.chatforge.spring_chatforge;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.vitormmartins.chatforge.ChatForgeApplication;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChatForgeApplicationTests {

  private final String username = generateUniqueUsername();

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String generateUniqueUsername() {
    return "TestUser" + System.currentTimeMillis();
  }

  @Test
  void contextLoads() {
    // Test to ensure the application context loads successfully
  }

  @Test
  void testApplicationStartsSuccessfully() {
    // Ensure the Spring application starts without throwing any exceptions
    ChatForgeApplication.main(new String[]{});
    assertTrue(true, "Application started successfully without throwing exceptions");
  }

  @Test
  void testOpenAPIDefinitionAnnotation() {
    // Test to validate the presence and metadata of the OpenAPIDefinition annotation
    OpenAPIDefinition openAPIDefinition = ChatForgeApplication.class.getAnnotation(OpenAPIDefinition.class);
    assertNotNull(openAPIDefinition,
            "OpenAPIDefinition annotation should be present on ChatForgeApplication class");
    assertEquals("ChatForge API", openAPIDefinition.info().title());
    assertEquals("1.0", openAPIDefinition.info().version());
    assertEquals("API Documentation for ChatForge Application", openAPIDefinition.info().description());
  }

  @Test
  void testUserRegistration() throws Exception {
    String userJson = createTestUserRegistrationRequest();

    mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson))
            .andExpect(status().isOk())
            .andExpect(content().string("User registered"));

  }

  private String createTestUserRegistrationRequest() {
    // Create test user registration request
    return String.format("""
            {
                "username": "%s",
                "password": "testPassword",
                "email": "%s@example.com"
            }""", username, username);
  }

  @Test
  void testJWTLogin() throws Exception {
    String loginJson = createTestUserRegistrationRequest();

    MvcResult result = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginJson))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
            .andReturn();

    String token = result.getResponse().getContentAsString();
    assertNotNull(token);
    assertEquals(3,
            token.split("\\.").length,
            "Basic JWT structure validation failed");
  }

  @Test
  void testCookieLogin() throws Exception {
    String loginJson = createTestUserRegistrationRequest();

    mockMvc.perform(post("/auth/login-cookie")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginJson))
            .andExpect(status().isOk())
            .andExpect(cookie().exists("auth_token"))
            .andExpect(content().string("Cookie set"));
  }

  @Test
  void testGetUser() throws Exception {
    String loginJson = createTestUserRegistrationRequest();

    MvcResult loginResult = mockMvc.perform(post("/auth/login")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(loginJson))
                                   .andReturn();

    String token = loginResult.getResponse().getContentAsString();

    // Test get user endpoint with JWT authentication
    mockMvc.perform(get("/get/user/" + username)
                    .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.username").value("TestUser"));
  }

  @Test
  void testGetUserWithCookieAuth() throws Exception {
    String loginJson = createTestUserRegistrationRequest();

    MvcResult loginResult = mockMvc.perform(post("/auth/login-cookie")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(loginJson))
                                   .andReturn();

    String authCookie = Objects.requireNonNull(loginResult.getResponse().getCookie("auth_token")).getValue();

    // Test get user endpoint with cookie authentication
    mockMvc.perform(get("/get/user/TestUser")
                    .cookie(new Cookie("auth_token", authCookie)))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.username").value("TestUser"));
  }

  @Test
  void testUnauthorizedAccess() throws Exception {
    // Test unauthorized access without authentication
    mockMvc.perform(get("/get/user/TestUser"))
            .andExpect(status().isUnauthorized());
  }
}
