package io.initialcapacity.emailverifier.testdatabasesupport

import io.initialcapacity.emailverifier.databasesupport.DatabaseTemplate
import org.jetbrains.exposed.sql.Database

fun testDatabaseTemplate(databaseName: String) = DatabaseTemplate(Database.connect(
    url = "jdbc:postgresql://localhost:5555/${databaseName}_test?user=emailverifier&password=emailverifier"
))
