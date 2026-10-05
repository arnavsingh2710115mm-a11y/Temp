package com.petfeet.service;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Hand-written thread-safe producer/consumer queue.
 * Request threads (producers) call enqueue(); the NotificationWorker thread (consumer) blocks in take().
 * All access to the shared LinkedList is guarded by the same monitor (synchronized + wait/notifyAll).
 */
public class NotificationQueue {

    /** Immutable unit of work. */
    public static final class Item {
        public final int userId;
        public final String message;
        Item(int userId, String message) { this.userId = userId; this.message = message; }
    }

    private final Queue<Item> items = new LinkedList<>();
    private boolean closed = false;

    public synchronized void enqueue(int userId, String message) {
        if (closed) return;
        items.add(new Item(userId, message));
        notifyAll();                       // wake the worker
    }

    /** Blocks until an item is available. Returns null once the queue is closed and empty. */
    public synchronized Item take() throws InterruptedException {
        while (items.isEmpty()) {
            if (closed) return null;
            wait();
        }
        return items.poll();
    }

    public synchronized int size() { return items.size(); }

    public synchronized void close() {
        closed = true;
        notifyAll();
    }
}
