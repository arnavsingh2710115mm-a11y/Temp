package com.petfeet.service;

import java.util.Date;

/** Thread-safe status object shown in the admin "Background Services" panel. All methods are synchronized. */
public class BackgroundStatus {
    private boolean notificationRunning;
    private boolean statisticsRunning;
    private long notificationsProcessed;
    private long statisticsRuns;
    private Date lastNotificationRun;
    private Date lastStatisticsRun;
    private String lastError = "";

    public synchronized void notificationRunning(boolean value) { notificationRunning = value; }
    public synchronized void statisticsRunning(boolean value) { statisticsRunning = value; }

    public synchronized void notificationProcessed() {
        notificationsProcessed++;
        lastNotificationRun = new Date();
    }

    public synchronized void notificationFailed(String error) { lastError = "Notification: " + error; }

    public synchronized void statisticsRefreshed() {
        statisticsRuns++;
        lastStatisticsRun = new Date();
    }

    public synchronized void statisticsFailed(String error) { lastError = "Statistics: " + error; }

    public synchronized boolean isNotificationRunning() { return notificationRunning; }
    public synchronized boolean isStatisticsRunning() { return statisticsRunning; }
    public synchronized long getNotificationsProcessed() { return notificationsProcessed; }
    public synchronized long getStatisticsRuns() { return statisticsRuns; }
    public synchronized Date getLastNotificationRun() { return lastNotificationRun; }
    public synchronized Date getLastStatisticsRun() { return lastStatisticsRun; }
    public synchronized String getLastError() { return lastError; }
}
