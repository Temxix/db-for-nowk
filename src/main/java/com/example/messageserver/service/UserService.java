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
        if (userRepository.findByName(user.getName()) != null) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }
        userRepository.save(user);
    }
    
    public List<String> getAllUserNames() {
        return userRepository.findAllNames();
    }
    
    public String getWelcomeMessage(String name) {
        Optional<User> user = Optional.ofNullable(userRepository.findByName(name));
        
        if (user.isEmpty()) {
            return null;
        }
        
        String welcomeMessage = "Добро пожаловать!";
        return encryptionService.encryptMessage(welcomeMessage, user.get().getPublicKey());
    }
    
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
    
    public String getUserPublicKey(String username) {
        User user = userRepository.findByName(username);
        return user != null ? user.getPublicKey() : null;
    }

    public List<String> getRecipients(String username) {
        User user = userRepository.findRecipientNamesByName(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        return user.getRecipients().stream()
                .map(User.Recipient::getRecipient)
                .toList();
    }
} 