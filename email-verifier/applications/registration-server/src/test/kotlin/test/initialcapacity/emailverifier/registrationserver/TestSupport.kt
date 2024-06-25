package test.initialcapacity.emailverifier.registrationserver

import io.initialcapacity.emailverifier.rabbitsupport.RabbitExchange
import io.initialcapacity.emailverifier.rabbitsupport.buildConnectionFactory
import io.initialcapacity.emailverifier.registration.RegistrationDataGateway
import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import io.initialcapacity.emailverifier.registrationserver.module
import io.initialcapacity.emailverifier.testdatabasesupport.testDatabaseTemplate
import io.ktor.server.testing.*
import java.net.URI

fun testApp(block: suspend ApplicationTestBuilder.() -> Unit) {
    val dbTemplate = testDatabaseTemplate("registration")
    val connectionFactory = buildConnectionFactory(URI("amqp://localhost:5672"))
    val requestExchange = RabbitExchange(
        name = "test-request-exchange",
        type = "direct",
        routingKeyGenerator = { _: String -> "42" },
    )

    testApplication {
        application {
            module(
                RegistrationRequestDataGateway(dbTemplate),
                RegistrationDataGateway(dbTemplate),
                connectionFactory,
                requestExchange
            )
        }
        block()
    }
}
