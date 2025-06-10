package com.example.messageserver.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String chatId;
    private String text;
    private boolean sentByMe;
    private LocalDateTime timestamp;
    private String hash;

    public Message(String text, boolean sentByMe, String chatId, String hash) {
        this.text = text;
        this.sentByMe = sentByMe;
        this.chatId = chatId;
        this.timestamp = LocalDateTime.now();
        this.hash = hash;
    }
} 