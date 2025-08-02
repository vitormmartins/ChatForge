package io.vitormmartins.chatforge.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.infrastructure.security.util.JwtUtil;
import io.vitormmartins.chatforge.web.controller.dto.LoginRequest;
import io.vitormmartins.chatforge.web.controller.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private AuthenticationManager authenticationManager;

  @MockitoBean
  private JwtUtil jwtUtil;

  @MockitoBean
  private UserApplicationService userApplicationService;

  @Test
  void testLogin() throws Exception {
    String username = "user";
    String password = "pass";
    String token = "jwt-token";

    doNothing().when(authenticationManager)
            .authenticate(Mockito.any());
    when(jwtUtil.generateToken(username)).thenReturn(token);

    LoginRequest request = new LoginRequest(username, password);

    mockMvc.perform(post("/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string(token));
  }

  @Test
  void testLoginWithCookie() throws Exception {
    String username = "user";
    String password = "pass";
    String token = "jwt-token";

    doNothing().when(authenticationManager).authenticate(Mockito.any());
    when(jwtUtil.generateToken(username)).thenReturn(token);

    LoginRequest request = new LoginRequest(username, password);

    mockMvc.perform(post("/v1/auth/login-cookie")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(cookie().value("auth_token", token))
            .andExpect(content().string("Authentication successful"));
  }

  @Test
  void testRegister() throws Exception {
    String username = "user";
    String password = "pass";
    String email = "user@example.com";
    io.vitormmartins.chatforge.application.user.dto.UserDto userDto =
            new io.vitormmartins.chatforge.application.user.dto.UserDto(1L, username, email, LocalDateTime.now());

    when(userApplicationService.registerUser(Mockito.any()))
            .thenReturn(userDto);
    RegisterRequest request = new RegisterRequest(username, email, password);

    mockMvc.perform(post("/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.username").value(username))
            .andExpect(jsonPath("$.email").value(email));
  }
}
