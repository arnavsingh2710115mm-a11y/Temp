package com.petfeet.service;

import com.petfeet.exception.PetFeetException;

/**
 * Same contract as DbNotificationService but the work is handed to the background NotificationWorker
 * through a shared synchronized queue, so the web request returns immediately (polymorphism + multithreading).
 */
public class QueuedNotificationService implements NotificationService {
    @Override
    public void notifyUser(int userId, String message) throws PetFeetException {
        BackgroundServiceManager.getInstance().getQueue().enqueue(userId, message);
    }
}
