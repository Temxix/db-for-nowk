package com.example.messageserver.controller;

import com.example.messageserver.model.Message;
import com.example.messageserver.service.MessageService;
import com.example.messageserver.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    
    private final MessageService messageService;
    
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        System.out.println("Received message: " + message);
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (message.getSender() == null || message.getSender().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (message.getRecipient() == null || message.getRecipient().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        messageService.addMessage(message);
        return ResponseEntity.ok(message);
    }
    
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMessages(
            @RequestParam String username,
            @RequestParam String recipient) {
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (recipient == null || recipient.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(messageService.getMessages(username, recipient));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An error occurred: " + e.getMessage());
    }
}