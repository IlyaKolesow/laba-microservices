package com.example.notificationservice.service;

import com.example.notificationservice.data.dto.NotificationCreationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-topic")
    public void listen(NotificationCreationDto dto) {
        notificationService.createNotification(dto);
    }

}
