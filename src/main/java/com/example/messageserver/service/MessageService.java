package com.example.messageserver.service;

import com.example.messageserver.model.Message;
import com.example.messageserver.dto.MessageResponse;
import com.example.messageserver.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    
    public void addMessage(Message message) {
        messageRepository.save(message);
    }
    
    public List<MessageResponse> getMessages(String username, String recipient) {
        List<Message> messagesFromUser = messageRepository.findBySenderAndRecipient(username, recipient);
        List<Message> messagesToUser = messageRepository.findBySenderAndRecipient(recipient, username);
        
        List<MessageResponse> responses = messagesFromUser.stream()
                .map(message -> new MessageResponse(message, true))
                .collect(Collectors.toList());
        
        responses.addAll(messagesToUser.stream()
                .map(message -> new MessageResponse(message, false))
                .collect(Collectors.toList()));
        
        return responses;
    }
} 