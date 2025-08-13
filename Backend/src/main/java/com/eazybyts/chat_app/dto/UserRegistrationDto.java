package com.eazybyts.chat_app.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String name;
    private String username;
    private String email;
    private String password;
}