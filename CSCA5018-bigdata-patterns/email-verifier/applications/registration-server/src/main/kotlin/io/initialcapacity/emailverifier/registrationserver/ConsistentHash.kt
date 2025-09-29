package io.initialcapacity.emailverifier.registrationserver

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import kotlin.math.abs

class ConsistentHash<T>(private val numberOfReplicas: Int, nodes: Collection<T>) {
    private val circle = TreeMap<Int, T>()

    init {
        for (node in nodes) {
            add(node)
        }
    }

    fun add(node: T) {
        for (i in 0 until numberOfReplicas) {
            circle[hash(node.toString() + i)] = node
        }
    }

    fun remove(node: T) {
        for (i in 0 until numberOfReplicas) {
            circle.remove(hash(node.toString() + i))
        }
    }

    fun get(key: Any): T {
        if (circle.isEmpty()) {
            throw NoSuchElementException("No nodes in the hash ring")
        }
        val hash = hash(key.toString())
        return circle.ceilingEntry(hash)?.value ?: circle.firstEntry().value
    }

    private fun hash(key: String): Int {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(key.toByteArray(StandardCharsets.UTF_8))
        return abs(hash.contentToString().hashCode())
    }
}
