package com.eazybyts.chat_app.services;

import lombok.Data;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {


    private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16; // 16 bytes for AES-CBC

    public EncryptedData encrypt(String plainText, byte[] secretKey) {
        try {
            // Generate random IV
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            // Create secret key spec
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            // Encrypt
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Return IV + encrypted data (both base64 encoded)
            return new EncryptedData(
                    Base64.getEncoder().encodeToString(iv),
                    Base64.getEncoder().encodeToString(encrypted)
            );

        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    public record EncryptedData(String iv, String encryptedData) {}


}


