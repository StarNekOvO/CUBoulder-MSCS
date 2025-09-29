plugins {
    id("email-verifier.java-conventions")
}

val ktorVersion: String by project

dependencies {
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}
