package io.vitormmartins.chatforge.application.user.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Faking implementation to test the contract of PasswordEncoderPort
class PasswordEncoderPortTest {

    static class DummyPasswordEncoder implements PasswordEncoderPort {
        @Override
        public String encode(String rawPassword) {
            if(rawPassword == null) {
                throw new IllegalArgumentException("rawPassword cannot be null");
            }
            // Simple encoding: reverse the string and prepend "encoded":
            return "encoded:" + new StringBuilder(rawPassword).reverse();
        }

        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            if(rawPassword == null || encodedPassword == null) {
                throw new IllegalArgumentException("Both raw and encoded passwords must be non-null");
            }
            return encode(rawPassword).equals(encodedPassword);
        }
    }

    private final PasswordEncoderPort encoder = new DummyPasswordEncoder();

    @Test
    @DisplayName("Encode method returns expected value")
    void testEncode() {
        String raw = "test123";
        String expected = "encoded:" + new StringBuilder(raw).reverse();
        String actual = encoder.encode(raw);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Encode with null rawPassword throws exception")
    void testEncodeNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> encoder.encode(null));
        assertEquals("rawPassword cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("Matches method returns true for valid match")
    void testMatchesTrue() {
        String raw = "password";
        String encoded = encoder.encode(raw);
        assertTrue(encoder.matches(raw, encoded));
    }

    @Test
    @DisplayName("Matches method returns false for non-matching passwords")
    void testMatchesFalse() {
        String raw = "password";
        String wrongEncoded = "encoded:wrong";
        assertFalse(encoder.matches(raw, wrongEncoded));
    }

    @Test
    @DisplayName("Matches with null rawPassword throws exception")
    void testMatchesNullRaw() {
        String encoded = encoder.encode("password");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> encoder.matches(null, encoded));
        assertEquals("Both raw and encoded passwords must be non-null", ex.getMessage());
    }

    @Test
    @DisplayName("Matches with null encodedPassword throws exception")
    void testMatchesNullEncoded() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> encoder.matches("password", null));
        assertEquals("Both raw and encoded passwords must be non-null", ex.getMessage());
    }
}