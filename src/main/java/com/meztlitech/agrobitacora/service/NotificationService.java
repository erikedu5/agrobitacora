package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.entity.NotificationEntity;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void notify(UserEntity user, String message) {
        try {
            NotificationEntity entity = new NotificationEntity();
            entity.setUser(user);
            entity.setMessage(message);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setRead(false);
            notificationRepository.save(entity);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<NotificationEntity> getNotifications(UserEntity user) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public void markAsRead(Long id, UserEntity user) {
        NotificationEntity entity = notificationRepository.findById(id).orElse(null);
        if (entity != null && entity.getUser().getId().equals(user.getId())) {
            entity.setRead(true);
            notificationRepository.save(entity);
        }
    }
}
