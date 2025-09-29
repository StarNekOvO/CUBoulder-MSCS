package test.initialcapacity.emailverifier.testsupport

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.GetResponse
import kotlinx.coroutines.delay
import io.initialcapacity.emailverifier.rabbitsupport.RabbitQueue
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

suspend fun ConnectionFactory.assertMessageReceived(
    queue: RabbitQueue,
    message: String,
    timeout: Duration = 50.milliseconds
) {
    newConnection().use { connection ->
        connection.createChannel()!!.use { channel ->
            var received: GetResponse? = null
            var elapsed = Duration.ZERO
            val delayDuration = 10.milliseconds

            while (received == null) {
                if (elapsed >= timeout) {
                    fail("No messages received")
                }
                delay(delayDuration)
                elapsed += delayDuration

                received = channel.basicGet(queue.name, true)
            }

            assertJsonEquals(received.body.decodeToString(), message)
        }
    }
}
