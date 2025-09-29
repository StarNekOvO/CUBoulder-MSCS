plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project

dependencies {
    implementation(project(":components:registration-request"))
    implementation(project(":components:database-support"))
    implementation(project(":components:serialization-support"))

    implementation("io.ktor:ktor-server-resources:$ktorVersion")

    testImplementation(project(":components:test-database-support"))
}
