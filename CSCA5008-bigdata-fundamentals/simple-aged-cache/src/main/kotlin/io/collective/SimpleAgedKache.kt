package io.collective

import java.time.Clock

class SimpleAgedKache(private val clock: Clock = Clock.systemDefaultZone()) {
    private var entries: Array<ExpirableEntry?> = arrayOfNulls(10)
    private var size: Int = 0

    // Method to add an entry to the cache
    @Synchronized
    fun put(key: Any, value: Any, retentionInMillis: Int) {
        val expirationTime = clock.instant().toEpochMilli() + retentionInMillis
        val entry = ExpirableEntry(key, value, expirationTime)

        // Check if the entry with the same key already exists and replace it
        for (i in 0 until size) {
            if (entries[i]?.key == key) {
                entries[i] = entry
                return
            }
        }

        // If the array is full, resize it
        if (size >= entries.size) {
            resize()
        }

        // Add the new entry and increment the size
        entries[size++] = entry
    }

    // Method to check if the cache is empty
    fun isEmpty(): Boolean {
        cleanUp() // Clean up expired entries before checking
        return size == 0
    }

    // Method to get the size of the cache
    fun size(): Int {
        cleanUp() // Clean up expired entries before getting the size
        return size
    }

    // Method to get an entry from the cache by key
    @Synchronized
    fun get(key: Any): Any? {
        cleanUp() // Clean up expired entries before searching
        for (i in 0 until size) {
            if (entries[i]?.key == key) {
                return entries[i]?.value
            }
        }
        return null // Return null if the key is not found
    }

    // Method to clean up expired entries
    @Synchronized
    private fun cleanUp() {
        val now = clock.instant().toEpochMilli()
        var newSize = 0
        for (i in 0 until size) {
            if (entries[i]?.expirationTime ?: 0 > now) {
                entries[newSize++] = entries[i] // Keep non-expired entries
            }
        }
        // Set the remaining entries to null
        for (i in newSize until size) {
            entries[i] = null
        }
        size = newSize // Update the size
    }

    // Method to resize the array when it is full
    private fun resize() {
        val newEntries = arrayOfNulls<ExpirableEntry>(entries.size * 2)
        System.arraycopy(entries, 0, newEntries, 0, entries.size) // Copy old entries to the new array
        entries = newEntries
    }

    // Inner class to represent an expirable cache entry
    private data class ExpirableEntry(val key: Any, val value: Any, val expirationTime: Long)
}
