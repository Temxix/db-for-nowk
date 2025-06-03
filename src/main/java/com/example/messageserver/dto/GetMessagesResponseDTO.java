package com.example.messageserver.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GetMessagesResponseDTO {
    private String text;
    private LocalDateTime timestamp;
    private boolean sentByMe;
    
    public GetMessagesResponseDTO(
        String text,
        LocalDateTime timestamp,
        boolean sentByMe
    ) {
        this.text = text;
        this.timestamp = timestamp;
        this.sentByMe = sentByMe;
    }
} 