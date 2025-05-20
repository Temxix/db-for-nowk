package com.example.messageserver.service;

import com.example.messageserver.model.Message;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    private final List<Message> messages = new ArrayList<>();
    
    public void addMessage(Message message) {
        messages.add(message);
    }
    
    public List<Message> getAllMessages() {
        return new ArrayList<>(messages);
    }
} 