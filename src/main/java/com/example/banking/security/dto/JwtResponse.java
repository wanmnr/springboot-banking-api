/**
 * This is a file description for JwtResponse.java
 * @author Wan
 * @version 1.0
 */
package com.example.banking.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
	private String token;
}