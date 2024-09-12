package com.java.project.websocketservice.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.project.websocketservice.dto.ChatMessageDto;
import com.java.project.websocketservice.redis.RedisWebSocketSessionStore;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketSessionManager {

    RedisWebSocketSessionStore redisWebSocketSessionStore;
    ObjectMapper objectMapper;

    public void sendMessageToSession(ChatMessageDto chatMessage) {
        try {
            String chatRoomId = chatMessage.getChatRoomId();
            Set<String> sessionIds = redisWebSocketSessionStore.getLocalSessionIdsByRoomId(chatRoomId);
            boolean sessionExistsLocally = sessionIds != null && !sessionIds.isEmpty();
            if (sessionExistsLocally) {
                for (String sessionId : sessionIds) {
                    WebSocketSession session = redisWebSocketSessionStore.getSession(sessionId);
                    if (session != null && session.isOpen()) {
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                    }
                }
            } else {
                sessionIds = redisWebSocketSessionStore.getSessionIdsByRoomId(chatRoomId);
                for (String sessionId : sessionIds) {
                    WebSocketSession session = redisWebSocketSessionStore.getSession(sessionId);
                    if (session != null && session.isOpen()) {
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
        }
    }
}
