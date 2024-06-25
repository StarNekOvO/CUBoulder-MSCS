package test.initialcapacity.emailverifier.rabbitsupport

import com.rabbitmq.client.MessageProperties
import io.initialcapacity.emailverifier.rabbitsupport.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

class ListenerTest {
    private val testQueue = RabbitQueue("test-queue")
    private val testExchange = RabbitExchange(
        name = "test-exchange",
        type = "direct",
        routingKeyGenerator = { _: String -> "42" },
    )
    private val factory = buildConnectionFactory(URI("amqp://localhost:5672"))

    @Before
    fun setUp() {
        factory.declareAndBind(exchange = testExchange, queue = testQueue, routingKey = "42")
    }

    @After
    fun tearDown() {
        factory.useChannel { channel ->
            channel.queueDelete(testQueue.name)
            channel.exchangeDelete(testExchange.name)
        }
    }

    @Test
    fun testListen() = runTest {
        factory.useChannel { channel ->
            val publishedMessage = "hi there"
            channel.basicPublish(
                testExchange.name,
                testExchange.routingKeyGenerator(publishedMessage),
                MessageProperties.PERSISTENT_BASIC,
                publishedMessage.toByteArray()
            )

            val messageChannel = Channel<String>()
            listen(channel, testQueue) { message ->
                launch {
                    messageChannel.send(message)
                }
            }

            launch {
                val receivedMessage = messageChannel.receive()

                assertEquals("hi there", receivedMessage)
            }
        }
    }
}
