package com.example.messageserver.dto;

import lombok.Data;

@Data
public class PostMessageResponseDTO {
    private String message;
    private String id;
    
    public PostMessageResponseDTO(String message, String id) {
        this.message = message;
        this.id = id;
    }
} 