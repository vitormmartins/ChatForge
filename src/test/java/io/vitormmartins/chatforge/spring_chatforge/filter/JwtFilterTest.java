package io.vitormmartins.chatforge.spring_chatforge.filter;

import io.vitormmartins.chatforge.spring_chatforge.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetailsService;

public class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtFilter jwtFilter;

   @BeforeEach
    public void setUp() throws Exception {
        try (AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
            // Initialization code if needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
