package com.java.project.notificationservice.kafka;


import com.java.project.notificationservice.model.ChatMessageDto;
import com.java.project.notificationservice.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KafkaConsumerService {

    NotificationService notificationService;

    @KafkaListener(topics = "chat-notifications", groupId = "notification-service-group")
    public void listen(ChatMessageDto message) {
        try {
            log.info("Received message: {}", message);
            notificationService.processNotification(message);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
        }
    }
}
