package test.initialcapacity.emailverifier.rabbitsupport

import kotlinx.coroutines.runBlocking
import io.initialcapacity.emailverifier.rabbitsupport.*
import org.junit.After
import org.junit.Before
import test.initialcapacity.emailverifier.testsupport.assertMessageReceived
import java.net.URI
import kotlin.test.Test

class TestPublishAction {
    private val testQueue = RabbitQueue("test-queue")
    private val testExchange = RabbitExchange(
        name = "test-exchange",
        type = "direct",
        routingKeyGenerator = { _: String -> "42" },
    )
    private val factory = buildConnectionFactory(URI("amqp://localhost:5672"))

    @Before
    fun setUp() {
        factory.declareAndBind(testExchange, testQueue, "42")
    }

    @After
    fun tearDown() {
        factory.useChannel { channel ->
            channel.queueDelete(testQueue.name)
            channel.exchangeDelete(testExchange.name)
        }
    }

    @Test
    fun testPublish() = runBlocking {
        val publishAction = publish(factory, testExchange)

        publishAction("""{"some": "message"}""")

        factory.assertMessageReceived(testQueue, """{"some": "message"}""")
    }
}
