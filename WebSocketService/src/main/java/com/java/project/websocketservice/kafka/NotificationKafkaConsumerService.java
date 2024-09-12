package com.java.project.websocketservice.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.project.websocketservice.dto.ChatMessageDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationKafkaConsumerService {

    WebSocketSessionManager webSocketSessionManager;
    ObjectMapper objectMapper;

    @KafkaListener(topics = "websocket-notifications", groupId = "websocket-group")
    public void listenNotifications(String notificationPayload) {
        try {
            ChatMessageDto notification = objectMapper.readValue(notificationPayload, ChatMessageDto.class);
            webSocketSessionManager.sendMessageToSession(notification);
        } catch (Exception e) {
            log.error("Error processing notification: {}", e.getMessage());
        }
    }
}
