package com.example.messageserver.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostMessageRequestDTO {
    private String username;
    private String recipient;
    private String content;
    
    public PostMessageRequestDTO(String username, String recipient, String content) {
        this.username = username;
        this.recipient = recipient;
        this.content = content;
    }
} 