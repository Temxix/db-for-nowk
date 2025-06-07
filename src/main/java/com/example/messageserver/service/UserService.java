package com.example.messageserver.service;

import com.example.messageserver.model.User;
import com.example.messageserver.repository.UserRepository;
import com.example.messageserver.exception.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    
    public UserService(UserRepository userRepository, EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }
    
    public void registerUser(User user) {
        log.info("Starting user registration process for user: {}", user.getName());
        
        log.debug("Checking if user already exists");
        if (userRepository.findByName(user.getName()) != null) {
            log.warn("User with name '{}' already exists", user.getName());
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        
        log.info("Saving new user to repository");
        userRepository.save(user);
        log.info("User '{}' successfully saved", user.getName());
    }
    
    public List<String> getAllUserNames() {
        return userRepository.findAllNames().stream()
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