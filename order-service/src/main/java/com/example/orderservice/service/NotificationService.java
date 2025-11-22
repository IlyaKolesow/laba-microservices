package com.example.orderservice.service;

import com.example.orderservice.data.dto.NotificationCreationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, NotificationCreationDto> kafkaTemplate;

    public void send(NotificationCreationDto creationDto) {
        kafkaTemplate.send("notification-topic", creationDto);
    }

}
