val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.6.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id ("org.flywaydb.flyway") version "8.5.7"
}

group = "com.monkeys"
version = "1.0"
application {
    mainClass.set("com.monkeys.terminal.ApplicationKt")
}

flyway {
    url = System.getenv("POSTGRES_URL")
    user = System.getenv("POSTGRES_USER")
    password = System.getenv("POSTGRES_PASSWORD")
    baselineOnMigrate=true
    locations = arrayOf("filesystem:resources/db/migration")
}

repositories {
    mavenCentral()
    maven ("https://jitpack.io")
}

dependencies {
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-server-locations:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.flywaydb:flyway-core:8.5.7")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.0")
    implementation("io.bkbn:kompendium-core:latest.release")//cool lib to gen openAPI docs, but still migrating to ktor 2.x
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    implementation("org.postgresql:postgresql:42.3.3")
}