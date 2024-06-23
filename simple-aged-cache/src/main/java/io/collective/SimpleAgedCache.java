package io.collective;

import java.time.Clock;

public class SimpleAgedCache {
    private final Clock clock;
    private ExpirableEntry[] entries; // Array to store cache entries
    private int size; // Number of current valid entries in the cache

    // Constructor with a custom clock
    public SimpleAgedCache(Clock clock) {
        this.clock = clock;
        this.entries = new ExpirableEntry[10]; // Initial capacity of 10
        this.size = 0;
    }

    // Default constructor with system clock
    public SimpleAgedCache() {
        this(Clock.systemDefaultZone());
    }

    // Method to add an entry to the cache
    public void put(Object key, Object value, int retentionInMillis) {
        long expirationTime = clock.instant().toEpochMilli() + retentionInMillis;
        ExpirableEntry entry = new ExpirableEntry(key, value, expirationTime);
        synchronized (this) {
            // Check if the entry with the same key already exists and replace it
            for (int i = 0; i < size; i++) {
                if (entries[i].key.equals(key)) {
                    entries[i] = entry;
                    return;
                }
            }
            // If the array is full, resize it
            if (size >= entries.length) {
                resize();
            }
            // Add the new entry and increment the size
            entries[size++] = entry;
        }
    }

    // Method to check if the cache is empty
    public boolean isEmpty() {
        cleanUp(); // Clean up expired entries before checking
        return size == 0;
    }

    // Method to get the size of the cache
    public int size() {
        cleanUp(); // Clean up expired entries before getting the size
        return size;
    }

    // Method to get an entry from the cache by key
    public Object get(Object key) {
        cleanUp(); // Clean up expired entries before searching
        synchronized (this) {
            for (int i = 0; i < size; i++) {
                if (entries[i].key.equals(key)) {
                    return entries[i].value;
                }
            }
        }
        return null; // Return null if the key is not found
    }

    // Method to clean up expired entries
    private void cleanUp() {
        long now = clock.instant().toEpochMilli();
        synchronized (this) {
            int newSize = 0;
            for (int i = 0; i < size; i++) {
                if (entries[i].expirationTime > now) {
                    entries[newSize++] = entries[i]; // Keep non-expired entries
                }
            }
            // Set the remaining entries to null
            for (int i = newSize; i < size; i++) {
                entries[i] = null;
            }
            size = newSize; // Update the size
        }
    }

    // Method to resize the array when it is full
    private void resize() {
        ExpirableEntry[] newEntries = new ExpirableEntry[entries.length * 2];
        System.arraycopy(entries, 0, newEntries, 0, entries.length); // Copy old entries to the new array
        entries = newEntries;
    }

    // Inner class to represent an expirable cache entry
    private static class ExpirableEntry {
        private final Object key;
        private final Object value;
        private final long expirationTime;

        // Constructor for ExpirableEntry
        public ExpirableEntry(Object key, Object value, long expirationTime) {
            this.key = key;
            this.value = value;
            this.expirationTime = expirationTime;
        }
    }
}
