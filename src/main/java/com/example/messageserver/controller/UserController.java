package com.example.messageserver.controller;

import com.example.messageserver.service.UserService;
import com.example.messageserver.dto.GetUsersResponseDTO;
import com.example.messageserver.dto.GetUserChatsResponseDTO;
import com.example.messageserver.dto.RegisterUserRequestDTO;
import com.example.messageserver.dto.UserResponseDTO;
import com.example.messageserver.dto.WelcomeMessageResponseDTO;
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
        log.info("Получен запрос на регистрацию пользователя: {}", userDTO != null ? userDTO.getName() : "null");
        
        if (!isValidUserDTO(userDTO)) {
            log.warn("Неверные данные пользователя");
            return ResponseEntity.badRequest().body("Неверные данные пользователя");
        }

        // Проверяем существование пользователя
        if (userService.getUserPublicKey(userDTO.getName()) != null) {
            log.warn("Пользователь с именем '{}' уже существует", userDTO.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Пользователь с таким именем уже существует");
        }
        
        try {
            log.info("Попытка регистрации пользователя в сервисе");
            UserResponseDTO responseDTO = userService.registerUser(userDTO);
            log.info("Пользователь успешно зарегистрирован");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Ошибка при регистрации пользователя: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Внутренняя ошибка сервера");
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
    public ResponseEntity<?> getWelcomeMessage(@RequestParam String name) {
        log.info("Получен запрос welcome для пользователя: {}", name);
        WelcomeMessageResponseDTO response = userService.getWelcomeMessage(name);
        if (response == null) {
            log.warn("Пользователь не найден: {}", name);
            return ResponseEntity.status(404).body("Пользователь не найден");
        }
        log.info("Отправлено приветственное сообщение для пользователя: {}", name);
        return ResponseEntity.ok(response);
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
} 