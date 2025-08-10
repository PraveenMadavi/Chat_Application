package com.samvad.chat_app.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EncryptedUserRequest {
    private String encryptedPayload;
    private String iv;
    private int token;
}