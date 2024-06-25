package io.initialcapacity.emailverifier.registrationserver

import com.rabbitmq.client.ConnectionFactory
import io.initialcapacity.emailverifier.databasesupport.DatabaseTemplate
import io.initialcapacity.emailverifier.rabbitsupport.*
import io.initialcapacity.emailverifier.registration.RegistrationConfirmationService
import io.initialcapacity.emailverifier.registration.RegistrationDataGateway
import io.initialcapacity.emailverifier.registration.register
import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestDataGateway
import io.initialcapacity.emailverifier.registrationrequest.RegistrationRequestService
import io.initialcapacity.emailverifier.registrationrequest.UuidProvider
import io.initialcapacity.emailverifier.registrationrequest.registrationRequest
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.*

class App

private val logger = LoggerFactory.getLogger(App::class.java)

fun main(): Unit = runBlocking {
    val port = System.getenv("PORT")?.toInt() ?: 8081
    val rabbitUrl = System.getenv("RABBIT_URL")?.let(::URI)
        ?: throw RuntimeException("Please set the RABBIT_URL environment variable")
    val databaseUrl = System.getenv("DATABASE_URL")
        ?: throw RuntimeException("Please set the DATABASE_URL environment variable")

    val dbConfig = DatabaseConfiguration(databaseUrl)
    val dbTemplate = DatabaseTemplate(dbConfig.db)

    val connectionFactory = buildConnectionFactory(rabbitUrl)
    val registrationRequestGateway = RegistrationRequestDataGateway(dbTemplate)
    val registrationGateway = RegistrationDataGateway(dbTemplate)

    val registrationNotificationExchange = RabbitExchange(
        name = "registration-notification-exchange",
        type = "direct",
        routingKeyGenerator = { _: String -> "42" },
    )
    val registrationNotificationQueue = RabbitQueue("registration-notification")
    connectionFactory.declareAndBind(exchange = registrationNotificationExchange, queue = registrationNotificationQueue, routingKey = "42")

    val registrationRequestExchange = RabbitExchange(
        // TODO - rename the request exchange (since you've already declared a direct exchange under the current name)
        name = "registration-request-exchange",
        // TODO - use a consistent hash exchange (x-consistent-hash)
        type = "direct",
        // TODO - calculate a routing key based on message content
        routingKeyGenerator = @Suppress("UNUSED_ANONYMOUS_PARAMETER") { message: String -> "42" },
    )
    // TODO - read the queue name from the environment
    val registrationRequestQueue = RabbitQueue("registration-request")
    // TODO - read the routing key from the environment
    connectionFactory.declareAndBind(exchange = registrationRequestExchange, queue = registrationRequestQueue, routingKey = "42")

    listenForRegistrationRequests(
        connectionFactory,
        registrationRequestGateway,
        registrationNotificationExchange,
        registrationRequestQueue
    )
    registrationServer(
        port,
        registrationRequestGateway,
        registrationGateway,
        connectionFactory,
        registrationRequestExchange
    ).start()
}

fun registrationServer(
    port: Int,
    registrationRequestGateway: RegistrationRequestDataGateway,
    registrationGateway: RegistrationDataGateway,
    connectionFactory: ConnectionFactory,
    registrationRequestExchange: RabbitExchange,
) = embeddedServer(
    factory = Jetty,
    port = port,
    module = { module(registrationRequestGateway, registrationGateway, connectionFactory, registrationRequestExchange) }
)

fun Application.module(
    registrationRequestGateway: RegistrationRequestDataGateway,
    registrationGateway: RegistrationDataGateway,
    connectionFactory: ConnectionFactory,
    registrationRequestExchange: RabbitExchange,
) {
    install(Resources)
    install(CallLogging)
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        json()
    }

    val publishRequest = publish(connectionFactory, registrationRequestExchange)

    install(Routing) {
        info()
        registrationRequest(publishRequest)
        register(RegistrationConfirmationService(registrationRequestGateway, registrationGateway))
    }
}

fun CoroutineScope.listenForRegistrationRequests(
    connectionFactory: ConnectionFactory,
    registrationRequestDataGateway: RegistrationRequestDataGateway,
    registrationNotificationExchange: RabbitExchange,
    registrationRequestQueue: RabbitQueue,
    uuidProvider: UuidProvider = { UUID.randomUUID() },
) {
    val publishNotification = publish(connectionFactory, registrationNotificationExchange)

    val registrationRequestService = RegistrationRequestService(
        gateway = registrationRequestDataGateway,
        publishNotification = publishNotification,
        uuidProvider = uuidProvider,
    )

    launch {
        logger.info("listening for registration requests")
        val channel = connectionFactory.newConnection().createChannel()
        listen(queue = registrationRequestQueue, channel = channel) { email ->
            logger.debug("received registration request for {}", email)
            registrationRequestService.generateCodeAndPublish(email)
        }
    }
}
