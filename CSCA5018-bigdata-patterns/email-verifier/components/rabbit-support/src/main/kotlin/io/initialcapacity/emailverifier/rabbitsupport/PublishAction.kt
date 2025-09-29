package io.initialcapacity.emailverifier.rabbitsupport

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties

typealias PublishAction = (String) -> Unit
data class RabbitExchange(
    val name: String,
    val type: String,
    val routingKeyGenerator: (String) -> String,
)

fun publish(factory: ConnectionFactory, exchange: RabbitExchange): PublishAction = fun(message: String) =
    factory.useChannel { channel ->
        channel.basicPublish(exchange.name, exchange.routingKeyGenerator(message), MessageProperties.PERSISTENT_BASIC, message.toByteArray())
    }
