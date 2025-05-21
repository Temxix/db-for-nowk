package com.example.messageserver.repository;

import com.example.messageserver.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByRecipient(String recipient);
    List<Message> findBySender(String sender);
    List<Message> findBySenderAndRecipient(String sender, String recipient);
} 