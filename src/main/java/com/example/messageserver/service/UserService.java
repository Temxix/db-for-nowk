package com.example.messageserver.service;

import com.example.messageserver.model.User;
import com.example.messageserver.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    
    public UserService(UserRepository userRepository, EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }
    
    public void registerUser(User user) {
        userRepository.save(user);
    }
    
    public List<String> getAllUserNames() {
        return userRepository.findAll().stream()
                .map(User::getName)
                .toList();
    }
    
    public String getWelcomeMessage(String name) {
        Optional<User> user = Optional.ofNullable(userRepository.findByName(name));
        
        if (user.isEmpty()) {
            return null;
        }
        
        String welcomeMessage = "Добро пожаловать!";
        return encryptionService.encryptMessage(welcomeMessage, user.get().getPublicKey());
    }
} 