package com.eazybyts.chat_app.controllers;

import com.eazybyts.chat_app.entities.Message;
import lombok.Data;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class InfoExchangeController {

    @MessageMapping("/messages")
    @SendTo("/sentMessages")
    public List<Message> sendMessages(@Payload UserInfo userInfo ){

        List<Message> tempMessages = new ArrayList<Message>();
        Message message = new Message();
        message.setId(userInfo.userId);
        message.setContent("Test message content");
        message.setRead(true);
        tempMessages.add(message);
        return tempMessages;
    }

    //DTO
    @Data
    private static class UserSession {
        private String userId;
        private String publicKey;
        private String aesKey;
        private String iv;
    }

    @Data
    private static class UserInfo {
        private Long userId;
        private String publicKey;
        private String aesKey;
        private String iv;
    }


}
