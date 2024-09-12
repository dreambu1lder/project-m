package com.java.project.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    private String chatRoomId;
    private String sender;
    private String content;
    private long timestamp;
    private MessageType type;
}
