package io.initialcapacity.emailverifier.fakesendgridendpoints

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

class FakeSendgrid

private val logger = LoggerFactory.getLogger(FakeSendgrid::class.java)

fun Application.fakeSendgridRoutes(authToken: String, mailCallback: suspend (String) -> Unit =  {}) {
    routing {
        get("/") { call.respond("Fake Sendgrid") }
        post("/v3/mail/send") {
            val headers = call.request.headers
            if (headers["Authorization"] != "Bearer $authToken") {
                return@post call.respond(HttpStatusCode.Unauthorized)
            }
            if (headers["Content-Type"]?.lowercase() != "application/json; charset=utf-8") {
                return@post call.respond(HttpStatusCode.BadRequest)
            }

            val body = call.receive<String>()
            mailCallback(body)
            logger.debug("email sent {}", body)

            call.respond(HttpStatusCode.Created)
        }
    }
}
