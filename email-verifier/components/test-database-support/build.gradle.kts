plugins {
    id("email-verifier.java-conventions")
}

val exposedVersion: String by project
val postgresVersion: String by project

dependencies {
    implementation(project(":components:database-support"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
}
