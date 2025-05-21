package com.example.messageserver.service;

import com.example.messageserver.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();
    
    public void registerUser(User user) {
        users.add(user);
    }
    
    public List<String> getAllUserNames() {
        return users.stream()
                .map(User::getName)
                .toList();
    }
    
    public String getWelcomeMessage(String name) {
        boolean isRegistered = users.stream()
                .anyMatch(user -> user.getName().equals(name));
        
        if (!isRegistered) {
            return null;
        }
        
        return "Добро пожаловать!";
    }
} 