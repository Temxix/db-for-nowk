package com.example.messageserver.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
public class GetMessagesResponseDTO extends ArrayList<GetMessagesResponseDTO.Message> {
    
    @Data
    public static class Message {
        private String text;
        private LocalDateTime timestamp;
        private boolean sentByMe;
        
        public Message(String text, LocalDateTime timestamp, boolean sentByMe) {
            this.text = text;
            this.timestamp = timestamp;
            this.sentByMe = sentByMe;
        }
    }
} 