plugins {
    kotlin("jvm") version "1.9.0"
    `maven-publish`
}

group = "com.lola.framework"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.lola.framework:Lola-Core:1.0-SNAPSHOT")
    implementation("com.lola.framework:Lola-Module:1.0-SNAPSHOT")
    implementation("com.lola.framework:Lola-Setting:1.0-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.+")
    implementation("io.github.oshai:kotlin-logging-jvm:5.+")
    implementation("org.slf4j:slf4j-api:2.+")
    implementation("it.unimi.dsi:fastutil:8.5.12")
    implementation("com.google.guava:guava:32.1.1-jre")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.lola.framework"
            artifactId = "Lola-Event"
            version = "1.0-SNAPSHOT"

            from(components["kotlin"])
        }
    }
}