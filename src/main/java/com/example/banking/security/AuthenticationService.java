/**
 * This is a file description for AuthenticationService.java
 * @author Wan
 * @version 1.0
 */
package com.example.banking.security;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.banking.entity.Role;
import com.example.banking.entity.Status;
import com.example.banking.entity.User;
import com.example.banking.repository.UserRepository;
import com.example.banking.security.dto.AuthenticationRequest;
import com.example.banking.security.dto.AuthenticationResponse;
//import com.example.banking.security.dto.LoginRequest;
import com.example.banking.security.dto.PasswordResetRequest;
import com.example.banking.security.dto.RegisterRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final JavaMailSender mailSender;

	public AuthenticationResponse register(RegisterRequest request) {
		User user = User.builder().firstname(request.getFirstname())
				.lastname(request.getLastname()).email(request.getEmail())
				.username(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(Role.USER).phone("").status(Status.INACTIVE).build();

		User savedUser = repository.save(user);
		String jwtToken = jwtService.generateToken(savedUser);
		return AuthenticationResponse.builder().token(jwtToken).build();
	}

	// public AuthenticationResponse login(LoginRequest request) {
	// authenticationManager.authenticate(
	// new
	// UsernamePasswordAuthenticationToken(request.getUsername()request.getEmail(),
	// request.getPassword()));
	// User user = repository.findByEmail(request.getEmail()).orElseThrow();
	// String jwtToken = jwtService.generateToken(user);
	// return AuthenticationResponse.builder().token(jwtToken).build();
	// }

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(),
						request.getPassword()));
		User user = repository.findByEmail(request.getEmail()).orElseThrow();
		String jwtToken = jwtService.generateToken(user);
		return AuthenticationResponse.builder().token(jwtToken).build();
	}

	public void resetPassword(PasswordResetRequest request) {
		User user = repository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		String resetToken = generateResetToken();
		user.setResetToken(resetToken);
		user.setResetTokenExpiryDate(LocalDateTime.now().plusHours(24));
		repository.save(user);

		sendPasswordResetEmail(user.getEmail(), resetToken);
	}

	private String generateResetToken() {
		return UUID.randomUUID().toString();
	}

	private void sendPasswordResetEmail(String email, String resetToken) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Password Reset Request");
		message.setText(
				"To reset your password, use this token: " + resetToken);
		mailSender.send(message);
	}
}