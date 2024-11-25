package com.example.banking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {
    @SuppressWarnings("unused")
	private Long userId;

    @NotNull
    @Size(min = 3, max = 50)
    private String username;
	
	@NotNull
    @Size(min = 8, max = 50)
    private String password;

    @NotNull
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private String email;

    @NotNull
    @Pattern(regexp = "^[+]?[0-9]{8,15}$")
    private String phone;


}