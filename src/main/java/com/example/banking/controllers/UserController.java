package com.example.banking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.banking.dto.UserDTO;
import com.example.banking.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing user operations")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(summary = "Create a new user", description = "Creates a new user with the provided details. Requires ADMIN role.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions") })
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> createUser(
			@Parameter(description = "User details", required = true) @Valid @RequestBody UserDTO userDTO) {
		UserDTO createdUser = userService.createUser(userDTO);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	@Operation(summary = "Get user by ID", description = "Retrieves a user by their ID. Accessible by ADMIN and USER roles.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserDTO.class))),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions") })
	@GetMapping("/{userId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<UserDTO> getUserById(
			@Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long userId) {
		UserDTO user = userService.getUserById(userId);
		return ResponseEntity.ok(user);
	}

	@Operation(summary = "Get all users", description = "Retrieves all users with pagination. Requires ADMIN role.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions") })
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<UserDTO>> getAllUsers(
			@Parameter(description = "Pagination parameters") Pageable pageable) {
		Page<UserDTO> users = userService.getAllUsers(pageable);
		return ResponseEntity.ok(users);
	}

	@Operation(summary = "Update user", description = "Updates an existing user. Accessible by ADMIN or the user themselves.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = UserDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions") })
	@PutMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#userId)")
	public ResponseEntity<UserDTO> updateUser(
			@Parameter(description = "ID of the user to update", required = true) @PathVariable Long userId,
			@Parameter(description = "Updated user details", required = true) @Valid @RequestBody UserDTO userDTO) {
		UserDTO updatedUser = userService.updateUser(userId, userDTO);
		return ResponseEntity.ok(updatedUser);
	}

	@Operation(summary = "Delete user", description = "Deletes a user by their ID. Requires ADMIN role.")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "User deleted successfully"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions") })
	@DeleteMapping("/{userId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteUser(
			@Parameter(description = "ID of the user to delete", required = true) @PathVariable Long userId) {
		userService.deleteUser(userId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Get user by email", description = "Retrieves a user by their email address. Requires ADMIN role.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserDTO.class))),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "403", description = "Insufficient permissions") })
	@GetMapping("/email/{email}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> getUserByEmail(
			@Parameter(description = "Email address of the user to retrieve", required = true) @PathVariable String email) {
		UserDTO user = userService.getUserByEmail(email);
		return ResponseEntity.ok(user);
	}
}