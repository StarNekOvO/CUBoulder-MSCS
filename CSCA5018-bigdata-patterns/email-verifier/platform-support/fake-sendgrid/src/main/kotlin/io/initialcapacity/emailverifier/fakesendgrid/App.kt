package io.initialcapacity.emailverifier.fakesendgrid

import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import kotlinx.coroutines.runBlocking
import io.initialcapacity.emailverifier.fakesendgridendpoints.fakeSendgridRoutes
import org.slf4j.LoggerFactory

class App

private val logger = LoggerFactory.getLogger(App::class.java)

fun main(): Unit = runBlocking {
    val port = System.getenv("PORT")?.toInt() ?: 9090

    logger.info("waiting for mail")
    embeddedServer(
        factory = Jetty,
        port = port,
        module = { fakeSendgridRoutes("super-secret") }
    ).start()
}
