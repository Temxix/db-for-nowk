package com.example.messageserver.dto;

import lombok.Data;

@Data
public class RegisterUserRequestDTO {
    private String name;
    private String public_key;
}
