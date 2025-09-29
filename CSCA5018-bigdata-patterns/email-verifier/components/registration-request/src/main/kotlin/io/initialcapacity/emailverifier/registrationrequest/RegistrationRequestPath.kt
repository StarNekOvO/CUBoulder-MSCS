package io.initialcapacity.emailverifier.registrationrequest

import io.initialcapacity.emailverifier.rabbitsupport.PublishAction
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/request-registration")
class RegistrationRequestPath(val email: String? = null)

fun Route.registrationRequest(publishRequest: PublishAction) {
    post<RegistrationRequestPath> {
        val parameters = call.receive<RegistrationRequestPath>()

        if (parameters.email != null) {
            publishRequest(parameters.email)
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}
