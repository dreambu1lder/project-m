package com.java.project.notificationservice.service;

import com.java.project.notificationservice.kafka.KafkaProducerService;
import com.java.project.notificationservice.model.ChatMessageDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationService {

    KafkaProducerService kafkaProducerService;

    public void processNotification(ChatMessageDto message) {
        log.info("Processing notification for message: {}", message);
        kafkaProducerService.sendNotification(message);
    }
}
