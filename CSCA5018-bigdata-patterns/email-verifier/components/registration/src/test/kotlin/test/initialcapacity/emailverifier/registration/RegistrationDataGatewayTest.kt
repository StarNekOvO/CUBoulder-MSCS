package test.initialcapacity.emailverifier.registration

import io.initialcapacity.emailverifier.registration.RegistrationDataGateway
import io.initialcapacity.emailverifier.testdatabasesupport.testDatabaseTemplate
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationDataGatewayTest {
    private val dbTemplate = testDatabaseTemplate("registration")
    private val gateway = RegistrationDataGateway(dbTemplate)

    @Before
    fun setUp() {
        dbTemplate.execute("delete from registrations")
    }

    @Test
    fun testSave() {
        gateway.save("email@example.com")

        val storedEmail = dbTemplate
            .queryOne("select email from registrations where email = 'email@example.com'") { it.getString("email") }
        assertEquals("email@example.com", storedEmail)
    }
}
