package test.initialcapacity.emailverifier.registrationserver

import io.initialcapacity.emailverifier.rabbitsupport.RabbitExchange
import io.initialcapacity.emailverifier.rabbitsupport.buildConnectionFactory
import io.initialcapacity.emailverifier.registration.RegistrationDataGateway
import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import io.initialcapacity.emailverifier.registrationserver.registrationServer
import io.initialcapacity.emailverifier.testdatabasesupport.testDatabaseTemplate
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import java.net.URI
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegisterTest {
    private val dbTemplate = testDatabaseTemplate("registration")
    private val requestGateway = RegistrationRequestDataGateway(dbTemplate)
    private val registrationGateway = RegistrationDataGateway(dbTemplate)


    private val connectionFactory = buildConnectionFactory(URI("amqp://localhost:5672"))
    private val requestExchange = RabbitExchange(
        name = "test-request-exchange",
        type = "direct",
        routingKeyGenerator = { _: String -> "42" },
    )
    private val confirmationCode = "cccccccc-1d21-442e-8fc0-a2259ec09190"

    private val regServer = registrationServer(
        port = 9120,
        registrationRequestGateway = requestGateway,
        registrationGateway = registrationGateway,
        connectionFactory = connectionFactory,
        registrationRequestExchange = requestExchange,
    )

    private val client = HttpClient(Java) {
        expectSuccess = false
    }

    @Before
    fun setUp() {
        dbTemplate.execute("delete from registration_requests")
        dbTemplate.execute("delete from registrations")
        requestGateway.save("pickles@example.com", UUID.fromString(confirmationCode))
        regServer.start(wait = false)
    }

    @After
    fun tearDown() {
        regServer.stop(50, 50)
    }

    @Test
    fun testRegister(): Unit = runBlocking {
        val status = client.post("http://localhost:9120/register") {
            headers {
                contentType(ContentType.Application.Json)
                setBody("""{"email": "pickles@example.com", "confirmationCode": "$confirmationCode"}""")
            }
        }.status

        assertTrue(status.isSuccess())

        val storedEmail = dbTemplate
            .queryOne("select email from registrations where email = 'pickles@example.com'") { it.getString("email") }

        assertEquals("pickles@example.com", storedEmail)
    }

    @Test
    fun testConfirmationWrongCode(): Unit = runBlocking {
        val status = client.post("http://localhost:9120/confirmation") {
            headers {
                contentType(ContentType.Application.Json)
                setBody(
                    """{"email": "pickles@example.com", "confirmationCode": "00000000-1d21-442e-8fc0-a2259ec09190"}"""
                )
            }
        }.status

        assertFalse(status.isSuccess())

        val registrationCount = dbTemplate
            .queryOne("select count(email) from registrations where email = 'pickles@example.com'") { it.getInt("count")}

        assertEquals(0, registrationCount)
    }
}
