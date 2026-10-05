package com.petfeet.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Statistics snapshot shared between the StatisticsWorker thread (writer) and request threads (readers). */
public class StatsCache {
    private Map<String, Object> snapshot = new HashMap<>();
    private long updatedAt = 0;

    public synchronized void update(Map<String, Object> newSnapshot) {
        this.snapshot = new HashMap<>(newSnapshot);
        this.updatedAt = System.currentTimeMillis();
    }

    public synchronized Map<String, Object> read() {
        return Collections.unmodifiableMap(new HashMap<>(snapshot));
    }

    public synchronized long getUpdatedAt() { return updatedAt; }
}
