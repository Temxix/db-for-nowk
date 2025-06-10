package com.example.messageserver.service;

import com.example.messageserver.model.User;
import com.example.messageserver.repository.UserRepository;
import com.example.messageserver.repository.MessageRepository;
import com.example.messageserver.dto.PostMessageRequestDTO;
import com.example.messageserver.dto.GetMessagesResponseDTO;
import org.springframework.stereotype.Service;
import com.example.messageserver.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final EncryptionService encryptionService;
    
    public MessageService(UserRepository userRepository, MessageRepository messageRepository, EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.encryptionService = encryptionService;
    }
    
    public String addMessage(PostMessageRequestDTO messageDTO) {
        log.info("Получен запрос на отправку сообщения от {} к {}", messageDTO.getUsername(), messageDTO.getRecipient());
        
        // Находим отправителя и получателя
        User sender = userRepository.findByName(messageDTO.getUsername());
        User recipient = userRepository.findByName(messageDTO.getRecipient());
        
        if (sender == null || recipient == null) {
            log.error("Отправитель или получатель не найден");
            throw new RuntimeException("Отправитель или получатель не найден");
        }
        
        String chatId = UUID.randomUUID().toString();
        
        // Создаем сообщение для получателя
        Message recipientMessage = new Message(messageDTO.getText(), false, chatId);
        messageRepository.save(recipientMessage);
        
        // Создаем сообщение для отправителя
        Message senderMessage = new Message(messageDTO.getText(), true, chatId);
        messageRepository.save(senderMessage);
        
        // Создаем или находим чат для получателя
        User.Chat recipientChat = findOrCreateChat(recipient, messageDTO.getUsername());
        recipientChat.setId(chatId);
        recipientChat.getMessageIds().add(recipientMessage.getId());
        recipientChat.setHasNewMessages(true);
        recipientChat.setLastActivity(LocalDateTime.now());
        
        // Создаем или находим чат для отправителя
        User.Chat senderChat = findOrCreateChat(sender, messageDTO.getRecipient());
        senderChat.setId(chatId);
        senderChat.getMessageIds().add(senderMessage.getId());
        senderChat.setLastActivity(LocalDateTime.now());
        
        // Сохраняем изменения
        userRepository.save(recipient);
        userRepository.save(sender);
        
        log.info("Сообщение успешно отправлено и сохранено");
        return recipientMessage.getId();
    }
    
    public GetMessagesResponseDTO getMessages(String username, String recipient) {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        
        User.Chat chat = findOrCreateChat(user, recipient);
        GetMessagesResponseDTO messages = new GetMessagesResponseDTO();
        
        // Получаем сообщения по их ID
        for (String messageId : chat.getMessageIds()) {
            Message message = messageRepository.findById(messageId).orElse(null);
            if (message != null) {
                String messageText = message.getText();
                String decryptedHash = null;
                
                // Если сообщение не от нас, расшифровываем хеш публичным ключом
                if (!message.isSentByMe()) {
                    decryptedHash = encryptionService.decryptMessage(message.getText(), user.getPublicKey());
                }
                
                messages.add(new GetMessagesResponseDTO.Message(
                    messageText,
                    decryptedHash,
                    message.getTimestamp(),
                    message.isSentByMe()
                ));
            }
        }
        
        // Помечаем чат как прочитанный
        chat.setHasNewMessages(false);
        userRepository.save(user);
        
        return messages;
    }
    
    private User.Chat findOrCreateChat(User user, String recipientName) {
        return user.getChats().stream()
            .filter(chat -> chat.getRecipient().equals(recipientName))
            .findFirst()
            .orElseGet(() -> {
                User.Chat newChat = new User.Chat();
                newChat.setRecipient(recipientName);
                newChat.setMessageIds(new ArrayList<>());
                newChat.setHasNewMessages(false);
                user.getChats().add(newChat);
                return newChat;
            });
    }
} 