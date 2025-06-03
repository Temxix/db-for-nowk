package com.example.messageserver.repository;

import com.example.messageserver.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByName(String name);
    
    @Query(value = "{}", fields = "{ 'name' : 1, '_id' : 0 }")
    List<User> findAllNames();
    
    @Query(value = "{ 'name' : ?0 }", fields = "{ 'recipients.recipient' : 1 }")
    User findRecipientNamesByName(String name);
}