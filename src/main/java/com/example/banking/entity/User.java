package com.example.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Entity
@Table(name = "users",
    indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_phone", columnList = "phone")
    }
)
@Data
@EqualsAndHashCode(of = "userId")
public class User {
    private static final Logger log = LoggerFactory.getLogger(User.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.inactive;

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
        return UserStatus.active.equals(this.status);
    }

    public boolean isAdmin() {
        return UserRole.admin.equals(this.role);
    }

    public void activate() {
        this.status = UserStatus.active;
    }

    public void suspend() {
        this.status = UserStatus.suspended;
    }

	
}