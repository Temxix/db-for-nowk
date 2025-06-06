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
    public ResponseEntity<User> registerUser(@RequestBody RegisterUserRequestDTO userDTO) {
        if (!isValidUserDTO(userDTO)) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = new User();
        user.setName(userDTO.getName().trim());
        
        try {
            userService.registerUser(user);
            return ResponseEntity.ok(user);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private boolean isValidUserDTO(RegisterUserRequestDTO userDTO) {
        return userDTO != null 
            && userDTO.getName() != null 
            && !userDTO.getName().trim().isEmpty();
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