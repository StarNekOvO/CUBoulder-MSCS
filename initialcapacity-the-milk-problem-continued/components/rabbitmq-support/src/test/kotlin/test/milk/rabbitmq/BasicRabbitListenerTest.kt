package test.milk.rabbitmq

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties
import io.milk.rabbitmq.BasicRabbitConfiguration
import io.milk.rabbitmq.BasicRabbitListener
import io.milk.rabbitmq.RabbitTestSupport
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class BasicRabbitListenerTest {
    private val testSupport = RabbitTestSupport()
    private val factory = ConnectionFactory().apply { useNio() }

    @Before
    fun before() {
        BasicRabbitConfiguration("test-exchange", "test-queue", "test-key").setUp()
        testSupport.purge("test-queue")
    }

    @Test
    fun listener() {
        val single = AtomicInteger()

        val listener =
            BasicRabbitListener("test-queue", TestHandler("single.1") { single.incrementAndGet() }, TestCancelHandler())
        listener.start()

        factory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                val body = "aBody".toByteArray()
                channel.basicPublish("test-exchange", "test-key", MessageProperties.BASIC, body)
            }
        }

        await().untilAtomic(single, equalTo(1))
        listener.stop()
    }

    @Test
    fun listenerMany() {
        val completed = AtomicInteger()

        val listeners = (1..4).map {
            BasicRabbitListener(
                "test-queue",
                TestHandler("many.$it") { completed.incrementAndGet() },
                TestCancelHandler()
            )
        }
        listeners.forEach { it.start() }

        factory.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                (1..50).map {
                    val body = "aBody.$it".toByteArray()
                    channel.basicPublish("test-exchange", "test-key", MessageProperties.PERSISTENT_BASIC, body)
                }
            }
        }

        await().untilAtomic(completed, equalTo(50))
        listeners.forEach { it.stop() }
    }

    @Test
    fun listenerManyManualAck() {
        val completed = AtomicInteger()

        val listeners = (1..4).map {
            BasicRabbitListener(
                "test-queue",
                ManualAckTestHandler("many.$it") { completed.incrementAndGet() },
                TestCancelHandler(),
                false
            )
        }
        listeners.forEach { it.start() }

        factory.newConnection().use { connection ->
            connection.createChannel().use { channel ->

                (1..50).map {
                    val body = "aBody.$it".toByteArray()
                    channel.basicPublish("test-exchange", "test-key", MessageProperties.PERSISTENT_BASIC, body)
                }
            }
        }

        await().atMost(500, TimeUnit.MILLISECONDS).untilAtomic(completed, equalTo(50))
        listeners.forEach { it.stop() }
    }
}
