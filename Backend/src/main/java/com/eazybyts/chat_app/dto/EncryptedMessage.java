package com.eazybyts.chat_app.dto;

import lombok.Data;

// DTO for encrypted data (only IV + encrypted payload)
@Data
public class EncryptedMessage {
    private String encryptedMessage;
    private String iv;
}
