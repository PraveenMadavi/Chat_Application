package com.samvaad.chat_app.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EncryptedLoginRequest {
    private String encryptedPayload;
    private String iv;
    private int token;
}