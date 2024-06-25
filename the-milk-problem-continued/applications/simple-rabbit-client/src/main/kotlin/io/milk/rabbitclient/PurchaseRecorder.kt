package io.milk.rabbitclient

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties
import io.milk.workflow.Worker
import org.slf4j.LoggerFactory

class PurchaseRecorder(
    private val factory: ConnectionFactory,
    private val routingKey: String,
    override val name: String = "sales-worker"
) : Worker<PurchaseTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    override fun execute(task: PurchaseTask) {
        logger.info("sending event. decrementing the {} quantity by {} for product_id={}", task.name, task.amount, task.id)

        try {
            factory.newConnection().use { connection ->
                connection.createChannel().use { channel ->
                    val body = mapper.writeValueAsString(task).toByteArray()

                    channel.basicPublish("products-exchange", routingKey, MessageProperties.PERSISTENT_BASIC, body)
                }
            }
        } catch (e: Exception) {
            logger.error(
                "shoot, failed to decrement the {} quantity by {} for product_id={}",
                task.name,
                task.amount,
                task.id
            )
            e.printStackTrace()
        }
    }
}
