package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.service.AuthenticationService;
import com.meztlitech.agrobitacora.service.NotificationService;
import com.meztlitech.agrobitacora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Log4j2
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<?>> getAll(@RequestHeader("Authorization") String token) {
        Long userId = authenticationService.verify("Bearer " + token).getId();
        UserEntity user = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(notificationService.getNotifications(user));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id,
                                           @RequestHeader("Authorization") String token) {
        Long userId = authenticationService.verify(token).getId();
        UserEntity user = userRepository.findById(userId).orElseThrow();
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok().build();
    }
}
