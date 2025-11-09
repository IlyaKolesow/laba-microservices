package com.example.notificationservice.controller;

import com.example.notificationservice.data.dto.NotificationCreationDto;
import com.example.notificationservice.data.dto.NotificationDto;
import com.example.notificationservice.exception.NotificationNotFoundException;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final ModelMapper mapper;

    @GetMapping
    public List<NotificationDto> getAllNotifications() {
        return notificationService.findAll().stream()
                .map(notification -> mapper.map(notification, NotificationDto.class))
                .toList();
    }

    @GetMapping("/{id}")
    public NotificationDto getNotification(@PathVariable String id) throws NotificationNotFoundException {
        return mapper.map(notificationService.findById(id), NotificationDto.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationDto createNotification(@RequestBody NotificationCreationDto dto) {
        return mapper.map(notificationService.createNotification(dto), NotificationDto.class);
    }

}
