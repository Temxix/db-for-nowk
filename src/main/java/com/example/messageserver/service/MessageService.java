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
        User.Recipient recipientObj = findOrCreateRecipient(recipient, messageDTO.getUsername());
        User.UserMessage message = new User.UserMessage();
        message.setText(messageDTO.getText());
        message.setSentByMe(false);
        recipientObj.getMessages().add(message);
        
        // Добавляем сообщение в список отправителя
        User.Recipient senderObj = findOrCreateRecipient(sender, messageDTO.getRecipient());
        User.UserMessage senderMessage = new User.UserMessage();
        senderMessage.setText(messageDTO.getText());
        senderMessage.setSentByMe(true);
        senderObj.getMessages().add(senderMessage);
        
        // Сохраняем изменения
        userRepository.save(recipient);
        userRepository.save(sender);
        
        return messageDTO.getUsername() + "_" + messageDTO.getRecipient() + "_" + LocalDateTime.now();
    }
    
    public List<GetMessagesResponseDTO> getMessages(String username, String recipient) {
        User user = messageRepository.findByUsernameAndRecipientName(username, recipient);
        if (user == null || user.getRecipients().isEmpty()) {
            return new ArrayList<>();
        }
        
        return user.getRecipients().get(0).getMessages().stream()
            .map(msg -> new GetMessagesResponseDTO(msg.getText(), LocalDateTime.now(), msg.isSentByMe()))
            .toList();
    }
    
    private User.Recipient findOrCreateRecipient(User user, String recipientName) {
        return user.getRecipients().stream()
            .filter(r -> r.getRecipient().equals(recipientName))
            .findFirst()
            .orElseGet(() -> {
                User.Recipient newRecipient = new User.Recipient();
                newRecipient.setRecipient(recipientName);
                newRecipient.setMessages(new ArrayList<>());
                user.getRecipients().add(newRecipient);
                return newRecipient;
            });
    }
} 