package com.example.messageserver.service;

import com.example.messageserver.model.User;
import com.example.messageserver.repository.UserRepository;
import com.example.messageserver.repository.MessageRepository;
import com.example.messageserver.dto.PostMessageRequestDTO;
import com.example.messageserver.dto.GetMessagesResponseDTO;
import org.springframework.stereotype.Service;
import com.example.messageserver.model.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    
    public MessageService(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }
    
    public String addMessage(PostMessageRequestDTO messageDTO) {
        // Находим отправителя и получателя
        User sender = userRepository.findByName(messageDTO.getUsername());
        User recipient = userRepository.findByName(messageDTO.getRecipient());
        
        if (sender == null || recipient == null) {
            throw new RuntimeException("Отправитель или получатель не найден");
        }
        
        // Создаем сообщение для получателя
        String chatId = messageDTO.getUsername() + "_" + messageDTO.getRecipient();
        Message recipientMessage = new Message(messageDTO.getText(), false, chatId);
        messageRepository.save(recipientMessage);
        
        // Создаем сообщение для отправителя
        Message senderMessage = new Message(messageDTO.getText(), true, chatId);
        messageRepository.save(senderMessage);
        
        // Добавляем сообщение в чат получателя
        User.Chat recipientChat = findOrCreateChat(recipient, messageDTO.getUsername());
        recipientChat.getMessageIds().add(recipientMessage.getId());
        recipientChat.setHasNewMessages(true);
        recipientChat.setLastActivity(LocalDateTime.now());
        
        // Добавляем сообщение в чат отправителя
        User.Chat senderChat = findOrCreateChat(sender, messageDTO.getRecipient());
        senderChat.getMessageIds().add(senderMessage.getId());
        senderChat.setLastActivity(LocalDateTime.now());
        
        // Сохраняем изменения
        userRepository.save(recipient);
        userRepository.save(sender);
        
        return recipientMessage.getId();
    }
    
    public GetMessagesResponseDTO getMessages(String username, String recipient) {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        
        User.Chat chat = findOrCreateChat(user, recipient);
        List<GetMessagesResponseDTO.Message> messages = new ArrayList<>();
        
        // Получаем сообщения по их ID
        for (String messageId : chat.getMessageIds()) {
            Message message = messageRepository.findById(messageId).orElse(null);
            if (message != null) {
                messages.add(new GetMessagesResponseDTO.Message(
                    message.getText(),
                    message.getTimestamp(),
                    message.isSentByMe()
                ));
            }
        }
        
        // Помечаем чат как прочитанный
        chat.setHasNewMessages(false);
        userRepository.save(user);
        
        return new GetMessagesResponseDTO(messages);
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
                newChat.setLastActivity(LocalDateTime.now());
                user.getChats().add(newChat);
                return newChat;
            });
    }
} 