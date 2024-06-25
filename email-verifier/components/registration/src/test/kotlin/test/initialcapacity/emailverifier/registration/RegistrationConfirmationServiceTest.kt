package test.initialcapacity.emailverifier.registration

import io.initialcapacity.emailverifier.registration.RegistrationConfirmationService
import io.initialcapacity.emailverifier.registration.RegistrationDataGateway
import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import io.initialcapacity.emailverifier.testdatabasesupport.testDatabaseTemplate
import org.junit.Before
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegistrationConfirmationServiceTest {
    private val dbTemplate = testDatabaseTemplate("registration")
    private val requestGateway = RegistrationRequestDataGateway(dbTemplate)
    private val registrationGateway = RegistrationDataGateway(dbTemplate)
    private val service = RegistrationConfirmationService(requestGateway, registrationGateway)

    @Before
    fun setUp() {
        dbTemplate.execute("delete from registrations")
        dbTemplate.execute("delete from registration_requests")
    }

    @Test
    fun testConfirm() {
        val uuid = UUID.fromString("55555555-1d21-442e-8fc0-a2259ec09190")
        requestGateway.save("there@example.com", uuid)

        assertTrue(service.confirm("there@example.com", uuid))

        assertEquals(1, countRegistrationsFor("there@example.com"))
    }

    @Test
    fun testConfirmNotThere() {
        assertFalse(service.confirm("not-there@example.com", UUID.randomUUID()))

        assertEquals(0, countRegistrationsFor("not-there@example.com"))
    }

    @Test
    fun testConfirmNoMatch() {
        val uuid = UUID.fromString("55555555-1d21-442e-8fc0-a2259ec09190")
        requestGateway.save("there@example.com", uuid)

        assertFalse(service.confirm("there@example.com", UUID.randomUUID()))

        assertEquals(0, countRegistrationsFor("there@example.com"))
    }

    private fun countRegistrationsFor(email: String): Int {
        return dbTemplate.queryOne("select count(1) as count from registrations where email = ?", email) {
            it.getInt("count")
        }!!
    }
}
