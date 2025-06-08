package com.example.messageserver.dto;

import lombok.Data;
import java.util.List;

@Data
public class GetUserChatsResponseDTO {
    private List<Chat> chats;
    
    @Data
    public static class Chat {
        private String recipient;
        private boolean hasNewMessages;
        private Long lastActivity;
        
        public Chat(String recipient, boolean hasNewMessages) {
            this.recipient = recipient;
            this.hasNewMessages = hasNewMessages;
        }

        public Chat(String recipient, boolean hasNewMessages, Long lastActivity) {
            this.recipient = recipient;
            this.hasNewMessages = hasNewMessages;
            this.lastActivity = lastActivity;
        }
    }
    
    public GetUserChatsResponseDTO(List<Chat> chats) {
        this.chats = chats;
    }
} 