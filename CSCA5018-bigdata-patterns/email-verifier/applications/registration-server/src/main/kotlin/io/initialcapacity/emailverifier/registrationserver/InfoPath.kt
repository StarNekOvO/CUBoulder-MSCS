package io.initialcapacity.emailverifier.registrationserver

import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/")
class InfoPath

fun Route.info() {
    get<InfoPath> {
        call.respond(mapOf("application" to "registration server"))
    }
}
