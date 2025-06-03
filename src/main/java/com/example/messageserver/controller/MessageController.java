package com.example.messageserver.controller;

import com.example.messageserver.service.MessageService;
import com.example.messageserver.dto.PostMessageResponseDTO;
import com.example.messageserver.dto.PostMessageRequestDTO;
import com.example.messageserver.dto.GetMessagesResponseDTO;
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
    public ResponseEntity<PostMessageResponseDTO> sendMessage(@RequestBody PostMessageRequestDTO message) {
        if (message.getText() == null || message.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (message.getUsername() == null || message.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (message.getRecipient() == null || message.getRecipient().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String messageId = messageService.addMessage(message);
        return ResponseEntity.ok(new PostMessageResponseDTO("Сообщение добавлено", messageId));
    }
    
    @GetMapping
    public ResponseEntity<List<GetMessagesResponseDTO>> getMessages(
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
            .body("Произошла ошибка: " + e.getMessage());
    }
}