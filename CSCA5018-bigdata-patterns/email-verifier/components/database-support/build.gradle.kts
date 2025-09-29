plugins {
    id("email-verifier.java-conventions")
}

val exposedVersion: String by project
val postgresVersion: String by project

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")

    testImplementation(project(":components:test-support"))
    testImplementation("org.postgresql:postgresql:$postgresVersion")
}
