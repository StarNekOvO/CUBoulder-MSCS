package test.initialcapacity.emailverifier.notification

import io.initialcapacity.emailverifier.notification.NotificationDataGateway
import io.initialcapacity.emailverifier.testdatabasesupport.testDatabaseTemplate
import org.junit.Before
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NotificationDataGatewayTest {
    private val dbTemplate = testDatabaseTemplate("notification")
    private val gateway = NotificationDataGateway(dbTemplate)

    @Before
    fun setUp() {
        dbTemplate.execute("delete from notifications")
    }

    @Test
    fun testSave() {
        val uuid = UUID.fromString("020b82f6-866f-47a6-90d4-359e866da123")

        gateway.save("a@example.com", uuid)

        assertEquals(uuid, gateway.find("a@example.com"))
    }

    @Test
    fun find_notFound() {
        assertNull(gateway.find("notthere@example.com"))
    }
}
