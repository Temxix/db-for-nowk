package com.example.messageserver.controller;

import com.example.messageserver.model.User;
import com.example.messageserver.service.UserService;
import com.example.messageserver.dto.GetUsersResponseDTO;
import com.example.messageserver.dto.RegisterUserRequestDTO;
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
        
        User user = new User();
        user.setName(userDTO.getName().trim());
        user.setPublicKey(userDTO.getPublic_key().trim());
        log.info("Created user object with name: {}", user.getName());
        
        try {
            log.info("Attempting to register user in service");
            userService.registerUser(user);
            log.info("User successfully registered");
            return ResponseEntity.ok(user);
        } catch (UserAlreadyExistsException e) {
            log.warn("User already exists: {}", user.getName());
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
            && userDTO.getPublic_key() != null
            && !userDTO.getPublic_key().trim().isEmpty();
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

    @GetMapping("/recipients")
    public ResponseEntity<List<String>> getRecipients(@RequestParam String username) {
        try {
            return ResponseEntity.ok(userService.getRecipients(username));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
} 