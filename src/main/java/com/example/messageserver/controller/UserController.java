package com.example.messageserver.controller;

import com.example.messageserver.model.User;
import com.example.messageserver.service.UserService;
import com.example.messageserver.dto.GetUsersResponseDTO;
import com.example.messageserver.dto.RegisterUserRequestDTO;
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
    public ResponseEntity<User> registerUser(@RequestBody RegisterUserRequestDTO userDTO) {
        if (userDTO.getName() == null || userDTO.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (userDTO.getPublicKey() == null || userDTO.getPublicKey().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = new User();
        user.setName(userDTO.getName());
        user.setPublicKey(userDTO.getPublicKey());
        
        try {
            userService.registerUser(user);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(null);
        }
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