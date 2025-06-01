package com.example.messageserver.controller;

import com.example.messageserver.model.User;
import com.example.messageserver.service.UserService;
import com.example.messageserver.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDTO userDTO) {
        if (userDTO.getName() == null || userDTO.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (userDTO.getPublicKey() == null || userDTO.getPublicKey().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = new User();
        user.setName(userDTO.getName());
        user.setPublicKey(userDTO.getPublicKey());
        
        userService.registerUser(user);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllUserNames() {
        return ResponseEntity.ok(userService.getAllUserNames());
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
} 