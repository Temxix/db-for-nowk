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
    private String username;
    private String recipient;
    private boolean isSentByMe;
    private LocalDateTime timestamp;
    private String content;
    
    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    public void setIsSentByMe(boolean isSentByMe) {
        this.isSentByMe = isSentByMe;
    }

    public boolean getIsSentByMe() {
        return isSentByMe;
    }
} 