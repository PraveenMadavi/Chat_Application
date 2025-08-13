package com.eazybyts.chat_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.eazybyts.chat_app.components.RSAUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RSAUtil rsaUtil;

    private final Map<String, UserSession> activeSessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/chat.register")
    public void register(@Payload RegisterMessage message) {
        UserSession session = new UserSession();
        session.setUserId(message.getUserId());
        session.setPublicKey(message.getPublicKey());
        activeSessions.put(message.getUserId(), session);
    }

    @MessageMapping("/chat.keyExchange")
    public void handleKeyExchange(@Payload KeyExchangeMessage message) throws Exception {
        UserSession sender = activeSessions.get(message.getSenderId());
        UserSession recipient = activeSessions.get(message.getRecipientId());

        if (recipient != null) {
            // Encrypt the AES key with recipient's public key
            String encryptedKey = rsaUtil.encrypt(message.getAesKey(), recipient.getPublicKey());

            KeyExchangeResponse response = new KeyExchangeResponse();
            response.setSenderId(message.getSenderId());
            response.setEncryptedAesKey(encryptedKey);
            response.setIv(message.getIv());

            messagingTemplate.convertAndSendToUser(
                    message.getRecipientId(),
                    "/queue/keyExchange",
                    response
            );
        }
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload EncryptedMessage message) throws Exception {
        // In a real app, you would decrypt the message with the AES key
        // and verify the sender, then re-encrypt for the recipient

        // For simplicity, we'll just forward the encrypted message
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(),
                "/queue/messages",
                message
        );
    }

    // DTO classes
    @Data
    public static class RegisterMessage {
        private String userId;
        private String publicKey;
    }

    @Data
    public static class KeyExchangeMessage {
        private String senderId;
        private String recipientId;
        private String aesKey;
        private String iv;
    }

    @Data
    public static class KeyExchangeResponse {
        private String senderId;
        private String encryptedAesKey;
        private String iv;
    }

    @Data
    public static class EncryptedMessage {
        private String messageId = UUID.randomUUID().toString();
        private String senderId;
        private String recipientId;
        private String encryptedContent;
        private String iv;
        private long timestamp = System.currentTimeMillis();
    }

    @Data
    private static class UserSession {
        private String userId;
        private String publicKey;
        private String aesKey;
        private String iv;
    }
}
