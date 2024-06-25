package io.initialcapacity.emailverifier.registration

import io.initialcapacity.emailverifier.databasesupport.DatabaseTemplate

class RegistrationDataGateway(private val dbTemplate: DatabaseTemplate) {
    fun save(email: String): Unit = dbTemplate.execute(
        //language=SQL
        "insert into registrations (email) values (?)",
        email
    )
}
