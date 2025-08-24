package com.eazybyts.chat_app.dto;

import com.eazybyts.chat_app.entities.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long id;
    private String text;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String timestamp;
    private String type; // CHAT, JOIN, LEAVE
    private String status; // SENT, DELIVERED, READ

    public static ChatMessage fromEntity(Message message){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(message.getId());
        chatMessage.setText(message.getContent());
//        chatMessage.setRoomId(message.getChatRoom().getId());
        chatMessage.setSenderId(message.getSenderId());
        chatMessage.setSenderName("Temp_sender_name");
        chatMessage.setTimestamp(message.getTime().toString());
        chatMessage.setType("CHAT");

        if (message.isStatus()){
            chatMessage.setStatus("delivered");
            if (message.isRead()){
                chatMessage.setStatus("read");
            }
        }

        return chatMessage;
    }
}
