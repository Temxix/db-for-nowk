package com.example.messageserver.service;

import com.example.messageserver.model.Message;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final List<Message> messages = new ArrayList<>();
    
    public void addMessage(Message message) {
        messages.add(message);
    }
    
    public List<Message> getAllMessages() {
        return new ArrayList<>(messages);
    }
    
    public List<Message> getMessagesByRecipient(String recipient) {
        return messages.stream()
                .filter(message -> message.getRecipient().equals(recipient))
                .collect(Collectors.toList());
    }
} 