package com.samvad.chat_app.dto;

import lombok.Data;

//To set AES key
@Data
public class EncryptedAesKey {
    private String encryptedAesKey;
//    private String sessionId; // Or any identifier
}
