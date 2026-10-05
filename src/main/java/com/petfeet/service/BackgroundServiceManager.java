package com.petfeet.service;

/** Starts and stops the two background threads. Singleton, controlled by AppLifecycleListener. */
public final class BackgroundServiceManager {
    private static final BackgroundServiceManager INSTANCE = new BackgroundServiceManager();

    private final NotificationQueue queue = new NotificationQueue();
    private final StatsCache statsCache = new StatsCache();
    private final BackgroundStatus status = new BackgroundStatus();
    private Thread notificationThread;
    private Thread statisticsThread;
    private StatisticsWorker statisticsWorker;

    private BackgroundServiceManager() { }

    public static BackgroundServiceManager getInstance() { return INSTANCE; }

    public synchronized void start() {
        if (notificationThread != null && notificationThread.isAlive()) return;
        notificationThread = new Thread(new NotificationWorker(queue, status), "petfeet-notification-worker");
        statisticsWorker = new StatisticsWorker(statsCache, status, 15_000);
        statisticsThread = new Thread(statisticsWorker, "petfeet-statistics-worker");
        notificationThread.setDaemon(true);
        statisticsThread.setDaemon(true);
        notificationThread.start();
        statisticsThread.start();
    }

    public synchronized void stop() {
        queue.close();
        if (statisticsWorker != null) statisticsWorker.stop();
        if (statisticsThread != null) statisticsThread.interrupt();
        if (notificationThread != null) notificationThread.interrupt();
    }

    public NotificationQueue getQueue() { return queue; }
    public StatsCache getStatsCache() { return statsCache; }
    public BackgroundStatus getStatus() { return status; }
}
