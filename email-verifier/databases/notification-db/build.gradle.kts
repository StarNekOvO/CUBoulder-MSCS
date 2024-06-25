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

tasks.register<org.flywaydb.gradle.task.FlywayMigrateTask>("devMigrate") {
    url = "jdbc:postgresql://localhost:5555/notification_dev?user=emailverifier&password=emailverifier"
}

tasks.register<org.flywaydb.gradle.task.FlywayCleanTask>("devClean") {
    url = "jdbc:postgresql://localhost:5555/notification_dev?user=emailverifier&password=emailverifier"
}

tasks.register<org.flywaydb.gradle.task.FlywayMigrateTask>("testMigrate") {
    url = "jdbc:postgresql://localhost:5555/notification_test?user=emailverifier&password=emailverifier"
}

tasks.register<org.flywaydb.gradle.task.FlywayCleanTask>("testClean") {
    url = "jdbc:postgresql://localhost:5555/notification_test?user=emailverifier&password=emailverifier"
}
