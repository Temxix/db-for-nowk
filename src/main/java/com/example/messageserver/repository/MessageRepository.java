package com.example.messageserver.repository;

import com.example.messageserver.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<User, String> {  
    @Query(value = "{ 'name' : ?0, 'recipients.recipient' : ?1 }", fields = "{ 'recipients.$' : 1 }")
    User findByUsernameAndRecipientName(String username, String recipient);
} 