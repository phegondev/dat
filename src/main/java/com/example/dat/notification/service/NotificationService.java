package com.example.dat.notification.service;

import com.example.dat.notification.dto.NotificationDTO;
import com.example.dat.users.entity.User;

public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO, User user);
}
