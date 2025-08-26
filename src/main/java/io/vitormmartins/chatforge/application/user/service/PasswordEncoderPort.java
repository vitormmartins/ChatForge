package io.vitormmartins.chatforge.application.user.service;

/**
 * Port interface for password encoding operations.
 * To be implemented by the infrastructure layer.
 */
public interface PasswordEncoderPort {
    
    /**
     * Encodes a raw password.
     * @param rawPassword the plain text password
     * @return the encoded password
     */
    String encode(String rawPassword);
    
    /**
     * Checks if a raw password matches an encoded password.
     * @param rawPassword the plain text password
     * @param encodedPassword the encoded password
     * @return true if passwords match
     */
    boolean matches(String rawPassword, String encodedPassword);
}
