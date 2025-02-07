/**
 * This is a file description for PasswordResetRequest.java
 * @author Wan
 * @version 1.0
 */
package com.example.banking.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {
	private String email;
}