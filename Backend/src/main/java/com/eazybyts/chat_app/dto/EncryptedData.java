package com.samvaad.chat_app.dto;

import lombok.*;

// Data with AES-key ... no need for long term ... just to test
@Getter
@Setter
@Data
@NoArgsConstructor
@ToString
public class EncryptedData {
    private String encryptedPayload;
    private String iv; //AES-->Vector
}
