package com.example.banking.entity;

import com.example.banking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void whenValidUser_thenShouldSave() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("$2a$10$abcdefghijklmnopqrstuvwxyz0123456789"); // BCrypt hash
        user.setEmail("test@example.com");
        user.setPhone("+1234567890");

        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getUserId());
        assertEquals("testuser", savedUser.getUsername());
    }

    @Test
    void whenInvalidEmail_thenShouldThrowException() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("$2a$10$abcdefghijklmnopqrstuvwxyz0123456789");
        user.setEmail("invalid-email");
        user.setPhone("+1234567890");

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(user);
            entityManager.flush();
        });
    }

    @Test
    void testUserStatusTransitions() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("$2a$10$abcdefghijklmnopqrstuvwxyz0123456789");
        user.setEmail("test@example.com");
        user.setPhone("+1234567890");

        assertEquals(UserStatus.inactive, user.getStatus());
        
        user.activate();
        assertEquals(UserStatus.active, user.getStatus());
        
        user.suspend();
        assertEquals(UserStatus.suspended, user.getStatus());
    }
}