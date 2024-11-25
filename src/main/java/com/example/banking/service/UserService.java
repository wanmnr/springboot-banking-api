package com.example.banking.service;

import com.example.banking.dto.UserDTO;
import com.example.banking.entity.UserStatus;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
	UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long userId, UserDTO userDTO);
    void deleteUser(Long userId);
    UserDTO getUserById(Long userId);
    UserDTO getUserByUsername(String username);
    Page<UserDTO> getAllUsers(Pageable pageable);
    List<UserDTO> getUsersByStatus(UserStatus status);
    UserDTO getUserByEmail(String email);
    void activateUser(Long userId);
    void suspendUser(Long userId);
    boolean isUsernameAvailable(String username);
    boolean isEmailAvailable(String email);
    boolean isPhoneAvailable(String phone);
    void changePassword(Long userId, String currentPassword, String newPassword);
}