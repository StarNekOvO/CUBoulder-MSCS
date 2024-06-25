package test.initialcapacity.emailverifier.notificationserver

import io.initialcapacity.emailverifier.fakesendgridendpoints.fakeSendgridRoutes
import io.initialcapacity.emailverifier.notificationserver.start
import io.initialcapacity.emailverifier.rabbitsupport.RabbitExchange
import io.initialcapacity.emailverifier.rabbitsupport.RabbitQueue
import io.initialcapacity.emailverifier.rabbitsupport.buildConnectionFactory
import io.initialcapacity.emailverifier.rabbitsupport.publish
import io.initialcapacity.emailverifier.testdatabasesupport.testDatabaseTemplate
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import test.initialcapacity.emailverifier.testsupport.MockServer
import test.initialcapacity.emailverifier.testsupport.assertJsonEquals
import java.net.URI
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class AppTest {
    private val sendgridUrl = URI("http://localhost:9021").toURL()
    private val rabbitUri = URI("amqp://localhost:5672")

    private val sendgridServer = MockServer(
        port = 9021,
        module = { fakeSendgridRoutes("super-secret") },
    )
    private val dbTemplate = testDatabaseTemplate("notification")

    @Before
    fun setUp() {
        sendgridServer.start()
        dbTemplate.execute("delete from notifications")
    }

    @After
    fun tearDown() {
        sendgridServer.stop()
    }

    @Test
    fun testApp() = runBlocking {
        val connectionFactory = buildConnectionFactory(rabbitUri)
        val exchange = RabbitExchange(
            "notification-test-exchange",
            type = "direct",
            routingKeyGenerator = { _: String -> "42" },
        )
        val notificationPublisher = publish(connectionFactory, exchange)

        start(
            sendgridUrl = sendgridUrl,
            sendgridApiKey = "super-secret",
            fromAddress = "from@example.com",
            connectionFactory = connectionFactory,
            registrationNotificationExchange = exchange,
            registrationNotificationQueue = RabbitQueue("notification-test-queue"),
            dbTemplate = dbTemplate,
        )

        notificationPublisher("""{"email": "to@example.com", "confirmationCode": "33333333-e89b-12d3-a456-426614174000"}""")

        val expectedCall = """
            {
                "personalizations": [{"to":[{"email": "to@example.com"}]}],
                "from": {"email": "from@example.com"},
                "subject": "Confirmation code",
                "content": [{
                    "type": "text/plain",
                    "value": "Your confirmation code is 33333333-e89b-12d3-a456-426614174000"
                }]
            }"""

        val receivedCall = sendgridServer.waitForCall(2.seconds)

        assertJsonEquals(expectedCall, receivedCall)
    }
}
