plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project

dependencies {
    implementation(project(":components:database-support"))

    implementation("io.ktor:ktor-client-core:$ktorVersion")

    testImplementation("io.ktor:ktor-client-java:$ktorVersion")
    testImplementation(project(":components:test-support"))
    testImplementation(project(":components:fake-sendgrid-endpoints"))
    testImplementation(project(":components:test-database-support"))
}
