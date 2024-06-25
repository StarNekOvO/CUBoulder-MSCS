package test.milk.rabbitmq

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import io.milk.rabbitmq.ChannelDeliverCallback
import org.slf4j.LoggerFactory

class TestHandler(private val name: String, private val function: () -> Unit) : ChannelDeliverCallback {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun setChannel(channel: Channel) {
    }

    override fun handle(consumerTag: String, message: Delivery) {
        logger.info("handling '${String(message.body)}' on channel=$name")
        function()
    }
}