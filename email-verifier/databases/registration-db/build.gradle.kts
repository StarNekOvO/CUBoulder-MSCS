import org.flywaydb.gradle.task.FlywayCleanTask
import org.flywaydb.gradle.task.FlywayMigrateTask

plugins {
    id("org.flywaydb.flyway") version "8.5.7"
}

repositories {
    mavenCentral()
}

val flywayMigration by configurations.creating
val postgresVersion: String by project

dependencies {
    flywayMigration("org.postgresql:postgresql:$postgresVersion")
}

flyway {
    configurations = arrayOf("flywayMigration")
}

tasks.register<FlywayMigrateTask>("devMigrate") {
    url = "jdbc:postgresql://localhost:5555/registration_dev?user=emailverifier&password=emailverifier"
}

tasks.register<FlywayCleanTask>("devClean") {
    url = "jdbc:postgresql://localhost:5555/registration_dev?user=emailverifier&password=emailverifier"
}

tasks.register<FlywayMigrateTask>("testMigrate") {
    url = "jdbc:postgresql://localhost:5555/registration_test?user=emailverifier&password=emailverifier"
}

tasks.register<FlywayCleanTask>("testClean") {
    url = "jdbc:postgresql://localhost:5555/registration_test?user=emailverifier&password=emailverifier"
}
