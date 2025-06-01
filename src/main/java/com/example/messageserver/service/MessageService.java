package com.example.messageserver.service;

import com.example.messageserver.model.Message;
import com.example.messageserver.repository.MessageRepository;
import com.example.messageserver.dto.PostMessageRequestDTO;
import com.example.messageserver.dto.GetMessagesResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final EncryptionService encryptionService;
    
    public MessageService(MessageRepository messageRepository, UserService userService, EncryptionService encryptionService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.encryptionService = encryptionService;
    }
    
    public String addMessage(PostMessageRequestDTO messageDTO) {
        // Get recipient's public key
        String recipientPublicKey = userService.getUserPublicKey(messageDTO.getRecipient());
        if (recipientPublicKey == null) {
            throw new RuntimeException("Получатель не найден");
        }
        
        // Encrypt the message
        String encryptedContent = encryptionService.encryptMessage(messageDTO.getContent(), recipientPublicKey);
        
        Message message = new Message();
        message.setContent(encryptedContent);
        message.setUsername(messageDTO.getUsername());
        message.setRecipient(messageDTO.getRecipient());
        message.setIsSentByMe(true);
        message.setTimestamp(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);
        
        // If username and recipient are different, create a second message
        if (!messageDTO.getUsername().equals(messageDTO.getRecipient())) {
            // Get sender's public key for the second message
            String senderPublicKey = userService.getUserPublicKey(messageDTO.getUsername());
            if (senderPublicKey == null) {
                throw new RuntimeException("Отправитель не найден");
            }
            
            // Encrypt the message with sender's public key
            String encryptedContentForSender = encryptionService.encryptMessage(messageDTO.getContent(), senderPublicKey);
            
            Message secondMessage = new Message();
            secondMessage.setContent(encryptedContentForSender);
            secondMessage.setUsername(messageDTO.getRecipient());
            secondMessage.setRecipient(messageDTO.getUsername());
            secondMessage.setIsSentByMe(false);
            secondMessage.setTimestamp(LocalDateTime.now());
            
            messageRepository.save(secondMessage);
        }
        
        return savedMessage.getId();
    }
    
    public List<GetMessagesResponseDTO> getMessages(String username, String recipient) {
        List<Message> messages = messageRepository.findByUsernameAndRecipient(
            username, recipient);
        List<GetMessagesResponseDTO> messageDTOs = new ArrayList<>();
        
        for (Message message : messages) {
            GetMessagesResponseDTO dto = new GetMessagesResponseDTO(
                message.getUsername(),
                message.getRecipient(),
                message.getTimestamp(),
                message.getIsSentByMe(),
                message.getContent()
            );
            messageDTOs.add(dto);
        }
        
        return messageDTOs;
    }
    
    public void deleteAllMessages() {
        messageRepository.deleteAll();
    }
} 