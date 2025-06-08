package com.example.messageserver.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserResponseDTO {
    private String name;
    private String publicKey;
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

    public UserResponseDTO() {
        this.chats = new ArrayList<>();
    }

    public UserResponseDTO(String name, String publicKey) {
        this.name = name;
        this.publicKey = publicKey;
        this.chats = new ArrayList<>();
    }
} 