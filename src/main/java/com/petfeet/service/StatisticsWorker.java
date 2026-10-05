package com.petfeet.service;

import com.petfeet.exception.PetFeetException;

/** Background thread: refreshes the admin statistics snapshot every few seconds. */
public class StatisticsWorker implements Runnable {
    private final StatsCache cache;
    private final BackgroundStatus status;
    private final long intervalMillis;
    private volatile boolean running = true;

    public StatisticsWorker(StatsCache cache, BackgroundStatus status, long intervalMillis) {
        this.cache = cache;
        this.status = status;
        this.intervalMillis = intervalMillis;
    }

    public void stop() { running = false; }

    @Override
    public void run() {
        AnalyticsService analytics = new AnalyticsService();
        status.statisticsRunning(true);
        try {
            while (running) {
                try {
                    cache.update(analytics.adminStats());
                    status.statisticsRefreshed();
                } catch (PetFeetException e) {
                    status.statisticsFailed(e.getMessage());
                }
                Thread.sleep(intervalMillis);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            status.statisticsRunning(false);
        }
    }
}
