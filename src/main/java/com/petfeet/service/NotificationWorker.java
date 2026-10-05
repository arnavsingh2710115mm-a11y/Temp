package com.petfeet.service;

import com.petfeet.exception.PetFeetException;

/** Background thread: takes queued notifications and stores them in the database. */
public class NotificationWorker implements Runnable {
    private final NotificationQueue queue;
    private final NotificationService delivery = new DbNotificationService();
    private final BackgroundStatus status;

    public NotificationWorker(NotificationQueue queue, BackgroundStatus status) {
        this.queue = queue;
        this.status = status;
    }

    @Override
    public void run() {
        status.notificationRunning(true);
        try {
            NotificationQueue.Item item;
            while ((item = queue.take()) != null) {
                try {
                    delivery.notifyUser(item.userId, item.message);
                    status.notificationProcessed();
                } catch (PetFeetException e) {
                    status.notificationFailed(e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            status.notificationRunning(false);
        }
    }
}
