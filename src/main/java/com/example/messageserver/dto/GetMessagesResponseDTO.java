package com.example.messageserver.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GetMessagesResponseDTO {
    private String username;
    private String recipient;
    private LocalDateTime timestamp;
    private boolean isSentByMe;
    private String content;
    
    public GetMessagesResponseDTO(
        String username,
        String recipient,
        LocalDateTime timestamp,
        boolean isSentByMe,
        String content
    ) {
        this.username = username;
        this.recipient = recipient;
        this.timestamp = timestamp;
        this.isSentByMe = isSentByMe;
        this.content = content;
    }
} 