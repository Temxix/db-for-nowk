package com.example.messageserver.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GetMessagesResponseDTO {
    private List<Message> messages = new ArrayList<>();
    
    public void add(Message message) {
        messages.add(message);
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public static class Message {
        private String text;
        private String decryptedHash;
        private LocalDateTime timestamp;
        private boolean sentByMe;
        
        public Message(String text, String decryptedHash, LocalDateTime timestamp, boolean sentByMe) {
            this.text = text;
            this.decryptedHash = decryptedHash;
            this.timestamp = timestamp;
            this.sentByMe = sentByMe;
        }
        
        public String getText() {
            return text;
        }
        
        public String getDecryptedHash() {
            return decryptedHash;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public boolean isSentByMe() {
            return sentByMe;
        }
    }
} 