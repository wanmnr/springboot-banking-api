package com.example.banking.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.banking.repository.UserRepository;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
class UserTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	private User createValidUser() {
		return User.builder().username("testuser")
				.password("$2a$10$abcdefghijklmnopqrstuvwxyz0123456789")
				.email("test@example.com").phone("+1234567890").build();
	}

	@Test
	void whenValidUser_thenShouldSave() {
		User user = createValidUser();
		User savedUser = userRepository.save(user);

		assertNotNull(savedUser.getUserId());
		assertEquals("testuser", savedUser.getUsername());
		assertEquals("test@example.com", savedUser.getEmail());
		assertEquals("+1234567890", savedUser.getPhone());
	}

	@Test
	void whenInvalidEmail_thenShouldThrowException() {
		User user = createValidUser();
		user.setEmail("invalid-email");

		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.persist(user);
			entityManager.flush();
		});
	}

	@Test
	void whenInvalidPhone_thenShouldThrowException() {
		User user = createValidUser();
		user.setPhone("invalid-phone");

		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.persist(user);
			entityManager.flush();
		});
	}

	@Test
	void testUserStatusTransitions() {
		User user = createValidUser();

		assertEquals(Status.INACTIVE, user.getStatus());
		assertFalse(user.isEnabled());
		assertTrue(user.isAccountNonExpired());
		assertTrue(user.isAccountNonLocked());
		assertTrue(user.isCredentialsNonExpired());

		user.activate();
		assertEquals(Status.ACTIVE, user.getStatus());
		assertTrue(user.isEnabled());
		assertTrue(user.isAccountNonLocked());

		user.suspend();
		assertEquals(Status.SUSPENDED, user.getStatus());
		assertFalse(user.isEnabled());
		assertFalse(user.isAccountNonLocked());
	}

	@Test
	void testUserRoles() {
		User user = createValidUser();

		assertEquals(Role.USER, user.getRole());
		assertFalse(user.isAdmin());

		user.setRole(Role.ADMIN);
		assertTrue(user.isAdmin());

		Collection<? extends GrantedAuthority> authorities = user
				.getAuthorities();
		assertTrue(
				authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
	}

	@Test
	void testUserDetails() {
		User user = createValidUser();

		assertEquals(user.getEmail(), user.getUsername()); // UserDetails
															// username is email
		assertEquals("$2a$10$abcdefghijklmnopqrstuvwxyz0123456789",
				user.getPassword());
	}

	@Test
	void testUserEquality() {
		User user1 = createValidUser();
		User user2 = createValidUser();
		User user3 = createValidUser();

		user1.setUserId(1L);
		user2.setUserId(1L);
		user3.setUserId(2L);

		assertEquals(user1, user2);
		assertNotEquals(user1, user3);
		assertNotEquals(user2, user3);
	}
}