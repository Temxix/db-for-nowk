package com.example.messageserver.controller;

import com.example.messageserver.service.UserService;
import com.example.messageserver.dto.GetUsersResponseDTO;
import com.example.messageserver.dto.GetUserChatsResponseDTO;
import com.example.messageserver.dto.RegisterUserRequestDTO;
import com.example.messageserver.dto.UserResponseDTO;
import com.example.messageserver.dto.CreateChatRequestDTO;
import com.example.messageserver.dto.ChatResponseDTO;
import com.example.messageserver.exception.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequestDTO userDTO) {
        log.info("Received registration request for user: {}", userDTO != null ? userDTO.getName() : "null");
        
        if (!isValidUserDTO(userDTO)) {
            log.warn("Invalid user DTO received");
            return ResponseEntity.badRequest().body("Неверные данные пользователя");
        }
        
        try {
            log.info("Attempting to register user in service");
            UserResponseDTO responseDTO = userService.registerUser(userDTO);
            log.info("User successfully registered");
            return ResponseEntity.ok(responseDTO);
        } catch (UserAlreadyExistsException e) {
            log.warn("User already exists: {}", userDTO.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера");
        }
    }
    
    private boolean isValidUserDTO(RegisterUserRequestDTO userDTO) {
        return userDTO != null 
            && userDTO.getName() != null 
            && !userDTO.getName().trim().isEmpty()
            && userDTO.getPublicKey() != null
            && !userDTO.getPublicKey().trim().isEmpty();
    }
    
    @GetMapping("/names")
    public ResponseEntity<GetUsersResponseDTO> getAllUserNames() {
        List<String> names = userService.getAllUserNames();
        GetUsersResponseDTO response = new GetUsersResponseDTO();
        response.setUsernames(names);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/welcome")
    public ResponseEntity<String> getWelcomeMessage(@RequestParam String name) {
        String message = userService.getWelcomeMessage(name);
        if (message == null) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }
        return ResponseEntity.ok(message);
    }
    
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllUsers() {
        userService.deleteAllUsers();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chats/list")
    public ResponseEntity<GetUserChatsResponseDTO> getChats(@RequestParam String username) {
        try {
            return ResponseEntity.ok(userService.getChats(username));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PostMapping("/chats")
    public ResponseEntity<?> createChat(@RequestBody CreateChatRequestDTO request) {
        log.info("Received request to create chat between {} and {}", 
            request.getUsername(), request.getRecipient());
        
        if (!isValidChatRequest(request)) {
            log.warn("Invalid chat creation request - empty username or recipient");
            return ResponseEntity.badRequest().body("Имя пользователя и получателя не могут быть пустыми");
        }
        
        try {
            ChatResponseDTO response = userService.createChat(request.getUsername(), request.getRecipient());
            log.info("Chat successfully created between {} and {} with id {}", 
                request.getUsername(), request.getRecipient(), response.getChatId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error creating chat: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Пользователь не найден: " + e.getMessage());
        }
    }

    private boolean isValidChatRequest(CreateChatRequestDTO request) {
        return request != null 
            && request.getUsername() != null 
            && !request.getUsername().trim().isEmpty()
            && request.getRecipient() != null 
            && !request.getRecipient().trim().isEmpty();
    }
} 