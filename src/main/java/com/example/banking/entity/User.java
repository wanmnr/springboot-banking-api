package com.example.banking.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", indexes = {
		@Index(name = "idx_user_email", columnList = "email"),
		@Index(name = "idx_user_username", columnList = "username"),
		@Index(name = "idx_user_phone", columnList = "phone")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "userId")
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(User.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@NotNull
	@Size(min = 3, max = 50)
	@Column(name = "username", nullable = false, unique = true, length = 50)
	private String username;

	private String firstname;
	private String lastname;
	private String resetToken;
	private LocalDateTime resetTokenExpiryDate;

	@NotNull
	@Size(min = 60, max = 255)
	@Column(name = "password", nullable = false)
	private String password;

	@NotNull
	@Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@NotNull
	@Pattern(regexp = "^[+]?[0-9]{8,15}$")
	@Column(name = "phone", nullable = false, unique = true, length = 20)
	private String phone;

	// @Enumerated(EnumType.STRING)
	// @Column(name = "role", nullable = false)
	@Builder.Default
	private Role role = Role.USER;

	// @Enumerated(EnumType.STRING)
	// @Column(name = "status", nullable = false)
	@Builder.Default
	private Status status = Status.INACTIVE;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	// Entity life cycle event listeners
	@PrePersist
	protected void onCreate() {
		log.debug("Creating new user: {}", username);
	}

	@PreUpdate
	protected void onUpdate() {
		log.debug("Updating user: {}", username);
	}

	// Custom methods
	public boolean isActive() {
		return Status.ACTIVE.equals(this.status);
	}

	public boolean isAdmin() {
		return Role.ADMIN.equals(this.role);
	}

	public void activate() {
		this.status = Status.ACTIVE;
	}

	public void suspend() {
		this.status = Status.SUSPENDED;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !Status.SUSPENDED.equals(this.status);
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return Status.ACTIVE.equals(this.status);
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

}