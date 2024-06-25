package test.initialcapacity.emailverifier.confirmation

import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import io.initialcapacity.emailverifier.testdatabasesupport.testDatabaseTemplate
import org.junit.Before
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationRequestDataGatewayTest {
    private val dbTemplate = testDatabaseTemplate("registration")
    private val gateway = RegistrationRequestDataGateway(dbTemplate)

    @Before
    fun setUp() {
        dbTemplate.execute("delete from registration_requests")
    }

    @Test
    fun testGet() {
        val uuid = UUID.fromString("11111111-e89b-12d3-a456-426614174000")

        gateway.save("email@example.com", uuid)

        assertEquals(uuid, gateway.find("email@example.com"))
        assertEquals(null, gateway.find("not_there@example.com"))
    }
}
