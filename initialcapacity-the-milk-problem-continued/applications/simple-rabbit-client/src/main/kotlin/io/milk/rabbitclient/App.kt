package io.milk.rabbitclient

import com.fasterxml.jackson.databind.SerializationFeature
import com.rabbitmq.client.ConnectionFactory
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.milk.workflow.WorkScheduler
import java.util.*

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(Routing) {
        get("/") {
            call.respondText { "ok!" }
        }
    }

    val workers = (1..4).map {
        PurchaseRecorder(ConnectionFactory().apply { useNio() }, "auto", "event-worker")
    }
    val scheduler = WorkScheduler(PurchaseGenerator(), workers, 30)
    scheduler.start()
}

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    val port = System.getenv("PORT")?.toInt() ?: 8083
    embeddedServer(Jetty, port, module = Application::module).start()
}
