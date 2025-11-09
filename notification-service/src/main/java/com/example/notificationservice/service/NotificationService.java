package com.example.notificationservice.service;

import com.example.notificationservice.data.dto.NotificationCreationDto;
import com.example.notificationservice.data.model.Notification;
import com.example.notificationservice.exception.NotificationNotFoundException;
import com.example.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification findById(String id) throws NotificationNotFoundException {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Не найдено уведомление с id = " + id));
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public Notification createNotification(NotificationCreationDto dto) {
        return notificationRepository.save(
                Notification.builder()
                        .type(dto.getType())
                        .message(dto.getMessage())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

}
