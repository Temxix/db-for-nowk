package com.example.messageserver.dto;

import com.example.messageserver.model.Message;
import lombok.Data;

@Data
public class MessageResponse {
    private Message message;
    private boolean isMine;
    
    public MessageResponse(Message message, boolean isMine) {
        this.message = message;
        this.isMine = isMine;
    }
} 