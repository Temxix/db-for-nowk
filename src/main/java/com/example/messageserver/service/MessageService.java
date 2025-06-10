package com.example.messageserver.service;

import com.example.messageserver.model.Message;
import com.example.messageserver.model.User;
import com.example.messageserver.repository.MessageRepository;
import com.example.messageserver.repository.UserRepository;
import com.example.messageserver.dto.PostMessageRequestDTO;
import com.example.messageserver.dto.GetMessagesResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    
    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }
    
    public String addMessage(PostMessageRequestDTO messageDTO) {
        log.info("Получен запрос на отправку сообщения от {} к {}", messageDTO.getUsername(), messageDTO.getRecipient());
        
        // Проверяем обязательные поля
        if (messageDTO.getHash() == null || messageDTO.getHash().trim().isEmpty()) {
            log.error("Хеш сообщения не указан");
            throw new RuntimeException("Хеш сообщения должен быть указан");
        }
        
        // Находим отправителя и получателя
        User sender = userRepository.findByName(messageDTO.getUsername());
        User recipient = userRepository.findByName(messageDTO.getRecipient());
        
        if (sender == null || recipient == null) {
            log.error("Отправитель или получатель не найден");
            throw new RuntimeException("Отправитель или получатель не найден");
        }
        
        String chatId = UUID.randomUUID().toString();
        
        // Создаем сообщение для получателя
        Message recipientMessage = new Message(messageDTO.getText(), false, chatId, messageDTO.getHash());
        messageRepository.save(recipientMessage);
        
        // Создаем сообщение для отправителя
        Message senderMessage = new Message(messageDTO.getText(), true, chatId, messageDTO.getHash());
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
                messages.add(new GetMessagesResponseDTO.Message(
                    message.getText(),
                    message.getTimestamp(),
                    message.isSentByMe(),
                    message.getHash()
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

    public void deleteAllMessages() {
        log.info("Удаление всех сообщений");
        messageRepository.deleteAll();
        log.info("Все сообщения успешно удалены");
    }
} 