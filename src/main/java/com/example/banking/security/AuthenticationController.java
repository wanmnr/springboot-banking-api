/**
 * This is a file description for AuthenticationController.java
 * @author Wan
 * @version 1.0
 */
package com.example.banking.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.banking.security.dto.AuthenticationRequest;
import com.example.banking.security.dto.AuthenticationResponse;
//import com.example.banking.security.dto.LoginRequest;
import com.example.banking.security.dto.PasswordResetRequest;
import com.example.banking.security.dto.RegisterRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(
			@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authenticationService.register(request));
	}

	// @PostMapping("/login")
	// public ResponseEntity<AuthenticationResponse> login(
	// @RequestBody LoginRequest request) {
	// return ResponseEntity.ok(authenticationService.login(request));
	// }

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(
			@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(authenticationService.authenticate(request));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(
			@RequestBody PasswordResetRequest request) {
		authenticationService.resetPassword(request);
		return ResponseEntity
				.ok("Password reset instructions sent to your email");
	}
}