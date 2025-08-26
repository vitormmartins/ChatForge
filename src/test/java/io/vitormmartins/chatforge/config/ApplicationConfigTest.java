package io.vitormmartins.chatforge.config;

import io.vitormmartins.chatforge.application.user.service.PasswordEncoderPort;
import io.vitormmartins.chatforge.application.user.service.UserApplicationService;
import io.vitormmartins.chatforge.domain.user.model.User;
import io.vitormmartins.chatforge.domain.user.repository.UserRepository;
import io.vitormmartins.chatforge.domain.user.service.UserDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationConfigTest {

    // Fake implementation for UserRepository.
    private static class DummyUserRepository implements UserRepository {
      /**
       * Saves a user entity.
       *
       * @param user the user to save
       * @return the saved user with generated ID (if new)
       */
      @Override
      public User save(User user) {
        return null;
      }

      /**
       * Finds a user by their unique identifier.
       *
       * @param id the user ID
       * @return optional user if found
       */
      @Override
      public Optional<User> findById(long id) {
        return Optional.empty();
      }

      /**
       * Finds a user by their username.
       *
       * @param username the username to search for
       * @return optional user if found
       */
      @Override
      public Optional<User> findByUsername(String username) {
        return Optional.empty();
      }

      /**
       * Checks if a username already exists.
       *
       * @param username the username to check
       * @return true if a username exists, false otherwise
       */
      @Override
      public boolean existsByUsername(String username) {
        return false;
      }

      /**
       * Deletes a user by their ID.
       *
       * @param id the user ID to delete
       */
      @Override
      public void deleteById(long id) {
      // Empty, only for tests purpose
      }


    }

    // Fake implementation for PasswordEncoderPort.
    private static class DummyPasswordEncoderPort implements PasswordEncoderPort {
      /**
       * Encodes a raw password.
       *
       * @param rawPassword the plain text password
       * @return the encoded password
       */
      @Override
      public String encode(String rawPassword) {
        return "";
      }

      /**
       * Checks if a raw password matches an encoded password.
       *
       * @param rawPassword     the plain text password
       * @param encodedPassword the encoded password
       * @return true if passwords match
       */
      @Override
      public boolean matches(String rawPassword, String encodedPassword) {
        return false;
      }
      // ...existing methods or stub implementations...
    }

    @Test
    @DisplayName("Test userDomainService bean creation and wiring")
    void userDomainService() {
        ApplicationConfig config = new ApplicationConfig();
        UserRepository dummyRepo = new DummyUserRepository();
        UserDomainService domainService = config.userDomainService(dummyRepo);
        assertNotNull(domainService, "UserDomainService should not be null");
        // Verify wiring via reflection since no public getter is assumed.
        try {
            var field = domainService.getClass().getDeclaredField("userRepository");
            field.setAccessible(true);
            Object repoValue = field.get(domainService);
            assertEquals(dummyRepo, repoValue, "UserRepository should be wired correctly in UserDomainService");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test userApplicationService bean creation and wiring")
    void userApplicationService() {
        ApplicationConfig config = new ApplicationConfig();
        UserRepository dummyRepo = new DummyUserRepository();
        UserDomainService domainService = config.userDomainService(dummyRepo);
        DummyPasswordEncoderPort dummyEncoder = new DummyPasswordEncoderPort();
        UserApplicationService appService = config.userApplicationService(dummyRepo, domainService, dummyEncoder);
        assertNotNull(appService, "UserApplicationService should not be null");
        // Verify wiring via reflection.
        try {
            var repoField = appService.getClass().getDeclaredField("userRepository");
            repoField.setAccessible(true);
            Object wiredRepo = repoField.get(appService);
            assertEquals(dummyRepo, wiredRepo, "UserRepository should be wired correctly in UserApplicationService");

            var domainField = appService.getClass().getDeclaredField("userDomainService");
            domainField.setAccessible(true);
            Object wiredDomain = domainField.get(appService);
            assertEquals(domainService, wiredDomain, "UserDomainService should be wired correctly in UserApplicationService");

            var encoderField = appService.getClass().getDeclaredField("passwordEncoder");
            encoderField.setAccessible(true);
            Object wiredEncoder = encoderField.get(appService);
            assertEquals(dummyEncoder, wiredEncoder, "PasswordEncoderPort should be wired correctly in UserApplicationService");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Reflection error: " + e.getMessage());
        }
    }
}