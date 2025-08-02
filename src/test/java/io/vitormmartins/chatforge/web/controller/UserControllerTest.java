package io.vitormmartins.chatforge.web.controller;

import io.vitormmartins.chatforge.application.user.dto.UserDto;
import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.config.TestSecurityConfig;
import io.vitormmartins.chatforge.config.TestSecurityComponentsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class }
)
@Import({TestSecurityConfig.class, TestSecurityComponentsConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserApplicationService userApplicationService;

    @Test
    void testGetUserByUsernameFound() throws Exception {
        String username = "user";
        UserDto userDto = new UserDto(1L, username, "user@example.com", LocalDateTime.now());

        when(userApplicationService.findUserByUsername(username))
                .thenReturn(Optional.of(userDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/api/users/" + username)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void testGetUserByUsernameNotFound() throws Exception {
        String username = "user";

        when(userApplicationService.findUserByUsername(username))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/api/users/" + username)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCheckUsernameAvailability() throws Exception {
        String username = "user";

        when(userApplicationService.isUsernameAvailable(username))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/api/users/check-username/" + username)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }
}
