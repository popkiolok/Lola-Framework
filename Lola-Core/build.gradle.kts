plugins {
    kotlin("jvm") version "1.9.0"
    `maven-publish`
}

group = "com.lola.framework"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.oshai:kotlin-logging-jvm:5.+")
    implementation("org.slf4j:slf4j-api:2.+")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.+")
    implementation("it.unimi.dsi:fastutil:8.5.12")
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.+")
    implementation("org.reflections:reflections:0.+")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.+")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.+")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.lola.framework"
            artifactId = "Lola-Core"
            version = "1.0-SNAPSHOT"

            from(components["kotlin"])
        }
    }
}