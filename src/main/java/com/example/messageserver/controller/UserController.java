package com.example.messageserver.controller;

import com.example.messageserver.model.User;
import com.example.messageserver.service.UserService;
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
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (user.getPublicKey() == null || user.getPublicKey().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
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
} 