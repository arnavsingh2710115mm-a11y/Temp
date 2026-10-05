package com.petfeet.service;

import com.petfeet.exception.PetFeetException;

/**
 * Contract for delivering a notification to a user.
 * Implementations: DbNotificationService (immediate) and QueuedNotificationService (asynchronous, background thread).
 */
public interface NotificationService {
    void notifyUser(int userId, String message) throws PetFeetException;
}
