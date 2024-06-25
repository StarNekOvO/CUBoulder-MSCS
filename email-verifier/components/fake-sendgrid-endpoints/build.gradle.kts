plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
}
