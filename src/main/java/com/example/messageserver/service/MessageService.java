package com.example.messageserver.service;

import com.example.messageserver.model.User;
import com.example.messageserver.repository.UserRepository;
import com.example.messageserver.repository.MessageRepository;
import com.example.messageserver.dto.PostMessageRequestDTO;
import com.example.messageserver.dto.GetMessagesResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    // private final UserService userService;
    // private final EncryptionService encryptionService;
    
    public MessageService(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        // this.userService = userService;
        // this.encryptionService = encryptionService;
    }
    
    public String addMessage(PostMessageRequestDTO messageDTO) {
        User sender = userRepository.findByName(messageDTO.getUsername());
        User recipient = userRepository.findByName(messageDTO.getRecipient());
        
        if (sender == null || recipient == null) {
            throw new RuntimeException("Отправитель или получатель не найден");
        }
        
        // Шифруем сообщение для получателя
        // String encryptedText = encryptionService.encryptMessage(messageDTO.getText(), recipient.getPublicKey());
        
        // Добавляем сообщение в список получателя
        User.Chat recipientObj = findOrCreateChat(recipient, messageDTO.getUsername());
        User.UserMessage message = new User.UserMessage(messageDTO.getText(), false);
        recipientObj.getMessages().add(message);
        
        // Добавляем сообщение в список отправителя
        User.Chat senderObj = findOrCreateChat(sender, messageDTO.getRecipient());
        User.UserMessage senderMessage = new User.UserMessage(messageDTO.getText(), true);
        senderObj.getMessages().add(senderMessage);
        
        // Сохраняем изменения
        userRepository.save(recipient);
        userRepository.save(sender);
        
        return messageDTO.getUsername() + "_" + messageDTO.getRecipient() + "_" + message.getTimestamp();
    }
    
    public GetMessagesResponseDTO getMessages(String username, String recipient) {
        User user = messageRepository.findByUsernameAndRecipientName(username, recipient);
        if (user == null || user.getChats().isEmpty()) {
            return new GetMessagesResponseDTO(new ArrayList<>());
        }
        
        List<GetMessagesResponseDTO.Message> messages = user.getChats().get(0).getMessages().stream()
            .map(msg -> new GetMessagesResponseDTO.Message(msg.getText(), msg.getTimestamp(), msg.isSentByMe()))
            .toList();
            
        return new GetMessagesResponseDTO(messages);
    }
    
    private User.Chat findOrCreateChat(User user, String recipientName) {
        return user.getChats().stream()
            .filter(r -> r.getRecipient().equals(recipientName))
            .findFirst()
            .orElseGet(() -> {
                User.Chat newChat = new User.Chat();
                newChat.setRecipient(recipientName);
                newChat.setMessages(new ArrayList<>());
                user.getChats().add(newChat);
                return newChat;
            });
    }
} 