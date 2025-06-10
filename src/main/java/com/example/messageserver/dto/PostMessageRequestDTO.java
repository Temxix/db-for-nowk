package com.example.messageserver.dto;

import lombok.Data;

@Data
public class PostMessageRequestDTO {
    private String username;
    private String recipient;
    private String text;
    private String hash;
    
    public PostMessageRequestDTO(String username, String recipient, String text) {
        this.username = username;
        this.recipient = recipient;
        this.text = text;
        this.hash = hash;
    }
} 