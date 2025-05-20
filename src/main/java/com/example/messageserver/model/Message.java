package com.example.messageserver.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private String content;
    private String sender;
    private LocalDateTime timestamp;
    
    public Message() {
        this.timestamp = LocalDateTime.now();
    }
} 