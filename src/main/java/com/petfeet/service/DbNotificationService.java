package com.petfeet.service;

import com.petfeet.dao.NotificationDAO;
import com.petfeet.exception.PetFeetException;
import com.petfeet.model.Notification;

/** Writes the notification straight into the database. */
public class DbNotificationService implements NotificationService {
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    public void notifyUser(int userId, String message) throws PetFeetException {
        notificationDAO.create(new Notification(userId, message));
    }
}
