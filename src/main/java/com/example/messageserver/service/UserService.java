package com.example.messageserver.service;

import com.example.messageserver.model.User;
import com.example.messageserver.repository.UserRepository;
import com.example.messageserver.exception.UserAlreadyExistsException;
import com.example.messageserver.dto.GetUserChatsResponseDTO;
import com.example.messageserver.dto.RegisterUserRequestDTO;
import com.example.messageserver.dto.UserResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.time.ZoneOffset;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    
    public UserService(UserRepository userRepository, EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }
    
    public UserResponseDTO registerUser(RegisterUserRequestDTO userDTO) {
        log.info("Starting user registration process for user: {}", userDTO.getName());
        
        log.debug("Checking if user already exists");
        if (userRepository.findByName(userDTO.getName()) != null) {
            log.warn("User with name '{}' already exists", userDTO.getName());
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        
        User user = new User();
        user.setName(userDTO.getName().trim());
        user.setPublicKey(userDTO.getPublicKey().trim());
        user.setChats(new ArrayList<>());
        
        log.info("Saving new user to repository");
        userRepository.save(user);
        log.info("User '{}' successfully saved", user.getName());
        
        return convertToDTO(user);
    }
    
    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO(user.getName(), user.getPublicKey());
        if (user.getChats() != null) {
            dto.setChats(user.getChats().stream()
                    .map(chat -> new UserResponseDTO.Chat(
                        chat.getRecipient(),
                        chat.isHasNewMessages(),
                        chat.getLastActivity() != null ? 
                            chat.getLastActivity().toEpochSecond(ZoneOffset.UTC) * 1000 : null))
                    .toList());
        }
        return dto;
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

    public GetUserChatsResponseDTO getChats(String username) {
        log.info("Starting getChats for user: {}", username);
        
        User user = userRepository.findRecipientNamesByName(username);
        if (user == null) {
            log.error("User not found: {}", username);
            throw new RuntimeException("Пользователь не найден");
        }
        log.debug("Found user: {}", username);
               
        // Get all users for additional chats
        List<String> allUsers = getAllUserNames();
        log.debug("Total users in system: {}", allUsers.size());
        log.debug("All users: {}", allUsers);
        
        // Find self-chat if it exists
        GetUserChatsResponseDTO.Chat selfChat = user.getChats().stream()
                .filter(chat -> chat.getRecipient().equals(username))
                .map(chat -> new GetUserChatsResponseDTO.Chat(chat.getRecipient(), chat.isHasNewMessages()))
                .findFirst()
                .orElse(new GetUserChatsResponseDTO.Chat(username, false));
        log.debug("Self chat created/retrieved for user: {}", username);
        
        // Get and sort existing chats (excluding self-chat)
        List<GetUserChatsResponseDTO.Chat> existingChats = user.getChats().stream()
                .filter(chat -> {
                    boolean isNotSelf = !chat.getRecipient().equals(username);
                    log.debug("Processing chat with recipient: {}, isNotSelf: {}", chat.getRecipient(), isNotSelf);
                    return isNotSelf;
                })
                .map(chat -> {
                    Long lastActivity = chat.getLastActivity() != null ? 
                        chat.getLastActivity().toEpochSecond(ZoneOffset.UTC) * 1000 : null;
                    log.debug("Chat with recipient: {}, lastActivity: {}", chat.getRecipient(), lastActivity);
                    return new GetUserChatsResponseDTO.Chat(
                        chat.getRecipient(), 
                        chat.isHasNewMessages(),
                        lastActivity);
                })
                .sorted((c1, c2) -> {
                    // Sort by lastActivity (newer first) if both chats have it
                    if (c1.getLastActivity() != null && c2.getLastActivity() != null) {
                        return c2.getLastActivity().compareTo(c1.getLastActivity());
                    }
                    return 0;
                })
                .toList();
        log.debug("Found {} existing chats for user: {}", existingChats.size(), username);
        log.debug("Existing chats: {}", existingChats);
        
        // Get recipients from existing chats
        Set<String> existingRecipients = existingChats.stream()
                .map(GetUserChatsResponseDTO.Chat::getRecipient)
                .collect(Collectors.toSet());
        log.debug("Existing recipients: {}", existingRecipients);
        
        // Create additional chats for users not in existing chats
        List<GetUserChatsResponseDTO.Chat> additionalChats = allUsers.stream()
                .filter(recipient -> {
                    boolean shouldInclude = !recipient.equals(username) && !existingRecipients.contains(recipient);
                    log.debug("Processing additional chat for recipient: {}, shouldInclude: {}", recipient, shouldInclude);
                    return shouldInclude;
                })
                .sorted()
                .map(recipient -> new GetUserChatsResponseDTO.Chat(recipient, false))
                .toList();
        log.debug("Created {} additional chats for user: {}", additionalChats.size(), username);
        log.debug("Additional chats: {}", additionalChats);
        
        // Combine all chats
        List<GetUserChatsResponseDTO.Chat> allChats = new ArrayList<>();
        allChats.add(selfChat);
        allChats.addAll(existingChats);
        allChats.addAll(additionalChats);
        log.info("Returning total of {} chats for user: {}", allChats.size(), username);
        log.debug("Final chat list: {}", allChats);
                
        return new GetUserChatsResponseDTO(allChats);
    }
} 