rootProject.name = "email-verifier"

include(
    "applications:notification-server",
    "applications:registration-server",
    "applications:benchmark",

    "components:registration-request",
    "components:notification",
    "components:registration",

    "components:database-support",
    "components:fake-sendgrid-endpoints",
    "components:rabbit-support",
    "components:serialization-support",
    "components:test-support",
    "components:test-database-support",

    "databases:notification-db",
    "databases:registration-db",

    "platform-support:fake-sendgrid",
)
