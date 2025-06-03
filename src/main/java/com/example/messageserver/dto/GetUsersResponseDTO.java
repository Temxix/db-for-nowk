package com.example.messageserver.dto;

import lombok.Data;
import java.util.List;

@Data
public class GetUsersResponseDTO {
    private List<String> usernames;
} 