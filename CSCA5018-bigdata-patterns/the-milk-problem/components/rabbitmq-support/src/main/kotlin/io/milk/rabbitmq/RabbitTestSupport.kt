package io.milk.rabbitmq

import com.rabbitmq.client.ConnectionFactory

class RabbitTestSupport {
    private val connectionFactory = ConnectionFactory().apply { useBlockingIo() }

    fun waitForConsumers(queue: String) {
        var count: Long
        do {
            count = messageCount(queue)
            Thread.sleep(500)
        } while (count > 0)
    }

    fun purge(queue: String) {
        connectionFactory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                channel.queuePurge(queue)
            }
        }
    }

    ///

    private fun messageCount(queue: String): Long {
        connectionFactory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                return channel.messageCount(queue)
            }
        }
    }
}