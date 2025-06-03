package com.example.messageserver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String publicKey;
    private List<Recipient> recipients;

    @Data
    public static class Recipient {
        private String recipient;
        private List<UserMessage> messages;
    }

    @Data
    public static class UserMessage {
        private String text;
        private boolean sentByMe;
    }
} 