package com.example.banking.service;

import com.example.banking.dto.UserDTO;
import com.example.banking.entity.User;
import com.example.banking.entity.UserStatus;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.exception.UserAlreadyExistsException;
import com.example.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository = null;
    private final PasswordEncoder passwordEncoder = null;

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        log.debug("Creating new user with username: {}", userDTO.getUsername());
        
        validateNewUser(userDTO);
        
        User user = new User();
        mapDtoToUser(userDTO, user);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        try {
        	User savedUser = userRepository.save(user);
            return convertToDto(savedUser);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        log.debug("Updating user with ID: {}", userId);
        
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        validateUserUpdate(userDTO, existingUser);
        mapDtoToUser(userDTO, existingUser);
        
        try {
        	User updatedUser = userRepository.save(existingUser);
            return convertToDto(updatedUser);
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.debug("Deleting user with ID: {}", userId);
        
        try {
            if (!userRepository.existsById(userId)) {
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }
            userRepository.deleteById(userId);
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public UserDTO getUserById(Long userId) {
        log.debug("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
            .map(this::convertToDto)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        return userRepository.findByUsername(username)
        		.map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDto);
    }

    @Override
    public List<UserDTO> getUsersByStatus(UserStatus status) {
        log.debug("Fetching users with status: {}", status);
        return userRepository.findByStatus(status)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public UserDTO getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
            .map(this::convertToDto)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        log.debug("Activating user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        user.activate();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void suspendUser(Long userId) {
        log.debug("Suspending user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        user.suspend();
        userRepository.save(user);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneAvailable(String phone) {
        return !userRepository.existsByPhone(phone);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.debug("Changing password for user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void validateNewUser(UserDTO userDTO) {
        if (!isUsernameAvailable(userDTO.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + userDTO.getUsername());
        }
        if (!isEmailAvailable(userDTO.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + userDTO.getEmail());
        }
        if (!isPhoneAvailable(userDTO.getPhone())) {
            throw new UserAlreadyExistsException("Phone number already exists: " + userDTO.getPhone());
        }
    }

    private void validateUserUpdate(UserDTO userDTO, User existingUser) {
        if (!existingUser.getUsername().equals(userDTO.getUsername()) && 
            !isUsernameAvailable(userDTO.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + userDTO.getUsername());
        }
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
            !isEmailAvailable(userDTO.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + userDTO.getEmail());
        }
        if (!existingUser.getPhone().equals(userDTO.getPhone()) && 
            !isPhoneAvailable(userDTO.getPhone())) {
            throw new UserAlreadyExistsException("Phone number already exists: " + userDTO.getPhone());
        }
    }

    private void mapDtoToUser(UserDTO dto, User user) {
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
    }
    
    private UserDTO convertToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        return dto;
    }
}