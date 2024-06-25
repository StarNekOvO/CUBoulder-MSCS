package io.initialcapacity.emailverifier.registration

import io.initialcapacity.serializationsupport.UUIDSerializer
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.util.*

class Register

private val logger = LoggerFactory.getLogger(Register::class.java)

@Serializable
@Resource("/register")
class RegisterPath(
    val email: String? = null,
    @Serializable(with = UUIDSerializer::class)
    val confirmationCode: UUID? = null,
)

fun Route.register(registrationConfirmationService: RegistrationConfirmationService) {
    post<RegisterPath> {
        val parameters = call.receive<RegisterPath>()

        if (parameters.email == null || parameters.confirmationCode == null) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val success = registrationConfirmationService.confirm(parameters.email, parameters.confirmationCode)

        if (success) {
            logger.info("successful registered ${parameters.email}")
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
