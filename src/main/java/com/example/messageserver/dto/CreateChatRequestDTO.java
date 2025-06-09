package com.example.messageserver.dto;

import lombok.Data;

@Data
public class CreateChatRequestDTO {
    private String username;
    private String recipient;
} 