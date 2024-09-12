package com.java.project.notificationservice.kafka;

import com.java.project.notificationservice.model.ChatMessageDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KafkaProducerService {

    KafkaTemplate<String, ChatMessageDto> kafkaTemplate;

    public void sendNotification(ChatMessageDto message) {
        kafkaTemplate.send("websocket-notifications", message);
        log.info("Sent notification: {}", message);
    }
}
