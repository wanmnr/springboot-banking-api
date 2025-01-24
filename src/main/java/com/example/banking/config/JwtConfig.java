package com.example.banking.config;

import java.util.Base64;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public String jwtSecretKey() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String generatedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        switch (activeProfile) {
            case "dev":
                // You can store this generated key for development
                System.out.println("Dev JWT Secret: " + generatedKey);
                return generatedKey;

            case "staging":
                // For staging, better to use environment variable
                return Optional.ofNullable(System.getenv("STAGING_JWT_SECRET"))
                    .orElseThrow(() -> new IllegalStateException("STAGING_JWT_SECRET environment variable not set"));

            case "prod":
                // For production, must use environment variable
                return Optional.ofNullable(System.getenv("PROD_JWT_SECRET"))
                    .orElseThrow(() -> new IllegalStateException("PROD_JWT_SECRET environment variable not set"));

            default:
                throw new IllegalStateException("Unknown profile: " + activeProfile);
        }
    }
}