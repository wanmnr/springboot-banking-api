package com.example.banking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.banking.entity.Status;
import com.example.banking.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
	Optional<User> findByPhone(String phone);
	List<User> findByStatus(Status status);

	@Query("SELECT u FROM User u WHERE u.status = 'active' AND u.role = 'admin'")
	List<User> findActiveAdmins();

	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	boolean existsByPhone(String phone);
}