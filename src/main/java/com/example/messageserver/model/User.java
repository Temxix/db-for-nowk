package com.example.messageserver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
        private String id;
        private String recipient;
        private List<String> messageIds;
        private boolean hasNewMessages;
        private LocalDateTime lastActivity;

        public Chat() {
            this.hasNewMessages = false;
            this.messageIds = new ArrayList<>();
        }
    }
} 