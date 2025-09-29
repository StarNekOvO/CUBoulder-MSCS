package test.initialcapacity.emailverifier.notification

import io.initialcapacity.emailverifier.fakesendgridendpoints.fakeSendgridRoutes
import io.initialcapacity.emailverifier.notification.Emailer
import io.ktor.client.*
import io.ktor.client.engine.java.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import test.initialcapacity.emailverifier.testsupport.MockServer
import test.initialcapacity.emailverifier.testsupport.assertJsonEquals
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertTrue


class EmailerTest {
    private val server = MockServer(
        port = 9021,
        module = { fakeSendgridRoutes("super-secret") },
    )

    @Before
    fun setUp() {
        server.start()
    }

    @After
    fun tearDown() {
        server.stop()
    }

    @Test
    fun testSend() = runBlocking {
        val emailer = Emailer(
            client = HttpClient(Java),
            sendgridUrl = URI("http://localhost:9021").toURL(),
            sendgridApiKey = "super-secret",
            fromAddress = "from@example.com",
        )

        val success = emailer.send("to@example.com", "Guess what?", "Hi there")

        assertTrue(success)

        val expectedResponse = """
            {
                "personalizations": [{"to":[{"email": "to@example.com"}]}],
                "from": {"email": "from@example.com"},
                "subject": "Guess what?",
                "content": [{
                    "type": "text/plain",
                    "value": "Hi there"
                }]
            }""".trimIndent()

        assertJsonEquals(expectedResponse, server.lastCallBody())
    }
}
