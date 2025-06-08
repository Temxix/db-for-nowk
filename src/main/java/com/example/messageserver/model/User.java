package com.example.messageserver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String publicKey;
    private List<Chat> chats;

    @Data
    public static class Chat {
        private String recipient;
        private List<UserMessage> messages;
        private boolean hasNewMessages;
        private LocalDateTime lastActivity;

        public Chat() {
            this.hasNewMessages = false;
            this.lastActivity = LocalDateTime.now();
        }
    }

    @Data
    public static class UserMessage {
        private String text;
        private boolean sentByMe;
        private LocalDateTime timestamp;

        public UserMessage(String text, boolean sentByMe) {
            this.text = text;
            this.sentByMe = sentByMe;
            this.timestamp = LocalDateTime.now();
        }
    }
} 