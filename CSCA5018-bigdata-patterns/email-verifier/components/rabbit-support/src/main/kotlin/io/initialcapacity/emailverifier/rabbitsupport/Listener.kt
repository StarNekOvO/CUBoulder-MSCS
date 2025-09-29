package io.initialcapacity.emailverifier.rabbitsupport

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import kotlinx.coroutines.runBlocking

data class RabbitQueue(val name: String)

fun listen(channel: Channel, queue: RabbitQueue, handler: suspend (String) -> Unit): String {
    val delivery = { _: String, message: Delivery -> runBlocking { handler(message.body.decodeToString()) } }
    val cancel = { _: String -> }

    return channel.basicConsume(queue.name, true, delivery, cancel)
}
