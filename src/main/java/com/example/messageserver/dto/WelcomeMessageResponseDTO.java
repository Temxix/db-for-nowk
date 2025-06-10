package com.example.messageserver.dto;

import lombok.Data;

@Data
public class WelcomeMessageResponseDTO {
    private String message;
    private String username;

    public WelcomeMessageResponseDTO(String message, String username) {
        this.message = message;
        this.username = username;
    }
} 