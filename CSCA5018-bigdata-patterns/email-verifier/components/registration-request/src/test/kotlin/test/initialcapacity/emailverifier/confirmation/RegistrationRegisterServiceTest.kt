package test.initialcapacity.emailverifier.confirmation

import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestService
import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import io.initialcapacity.emailverifier.testdatabasesupport.testDatabaseTemplate
import org.junit.Before
import test.initialcapacity.emailverifier.testsupport.assertJsonEquals
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationRegisterServiceTest {
    private val dbTemplate = testDatabaseTemplate("registration")

    @Before
    fun setUp() {
        dbTemplate.execute("delete from registration_requests")
    }

    @Test
    fun testGenerateCodeAndPublish() {
        val uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        var publishedMessage: String? = null

        val gateway = RegistrationRequestDataGateway(dbTemplate)
        val publish = { message: String -> publishedMessage = message }
        val uuidProvider = {  -> uuid }

        val expectedMessage = """
            {
              "email":"test@example.com",
              "confirmationCode":"123e4567-e89b-12d3-a456-426614174000"
            }
            """.trimIndent()

        val service = RegistrationRequestService(
            gateway,
            publish,
            uuidProvider,
        )

        service.generateCodeAndPublish("test@example.com")

        assertEquals(uuid, gateway.find("test@example.com"))
        assertJsonEquals(expectedMessage, publishedMessage)
    }
}
