package com.eazybyts.chat_app.dto;

import com.eazybyts.chat_app.entities.Message;
import com.eazybyts.chat_app.entities.Participant;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String avatar;
    private String lastMessage;
    private String lastMessageTime;
    private Integer unreadCount;
    private Boolean isOnline;
    private Boolean isTyping;
    private String lastSeen;
    private Boolean isGroup;
    private List<Participant> participants = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
}
