package com.example.messageserver.controller;

import com.example.messageserver.service.MessageService;
import com.example.messageserver.dto.PostMessageResponseDTO;
import com.example.messageserver.dto.PostMessageRequestDTO;
import com.example.messageserver.dto.GetMessagesResponseDTO;
import com.example.messageserver.dto.GetMessageIdsResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    
    private final MessageService messageService;
    
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    @PostMapping
    public ResponseEntity<?> postMessage(@RequestBody PostMessageRequestDTO message) {
        // Проверяем валидность данных
        if (message.getText() == null || message.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new PostMessageResponseDTO("Текст сообщения не может быть пустым", null));
        }
        if (message.getUsername() == null || message.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new PostMessageResponseDTO("Имя отправителя не может быть пустым", null));
        }
        if (message.getRecipient() == null || message.getRecipient().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new PostMessageResponseDTO("Имя получателя не может быть пустым", null));
        }
        if (message.getHash() == null || message.getHash().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new PostMessageResponseDTO("Хеш сообщения должен быть указан", null));
        }
        
        try {
            String messageId = messageService.addMessage(message);
            return ResponseEntity.ok(new PostMessageResponseDTO("Сообщение успешно отправлено", messageId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new PostMessageResponseDTO(e.getMessage(), null));
        }
    }
    
    @GetMapping
    public ResponseEntity<GetMessagesResponseDTO> getMessages(
            @RequestParam String username,
            @RequestParam String recipient) {
        // Проверяем валидность параметров
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (recipient == null || recipient.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            GetMessagesResponseDTO messages = messageService.getMessages(username, recipient);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/ids")
    public ResponseEntity<?> getMessageIds(
            @RequestParam String username,
            @RequestParam String recipient) {
        try {
            return ResponseEntity.ok(messageService.getMessageIds(username, recipient));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{username}/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable String username,
            @PathVariable String messageId) {
        try {
            messageService.deleteMessage(username, messageId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/chat")
    public ResponseEntity<?> deleteChat(
            @RequestParam String username,
            @RequestParam String recipient) {
        try {
            messageService.deleteChat(username, recipient);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Произошла ошибка: " + e.getMessage());
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllMessages() {
        try {
            messageService.deleteAllMessages();
            return ResponseEntity.ok("Все сообщения успешно удалены");
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка при удалении сообщений: " + e.getMessage());
        }
    }
}