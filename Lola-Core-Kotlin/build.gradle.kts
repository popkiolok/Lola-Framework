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
    //implementation("com.github.popkiolok:Lola-Core:master-SNAPSHOT")
    implementation("com.lola.framework:Lola-Core:1.0-SNAPSHOT")

    implementation("io.github.oshai:kotlin-logging-jvm:5.+")
    implementation("org.slf4j:slf4j-api:2.+")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.+")
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("it.unimi.dsi:fastutil:8.5.12")
    implementation("org.reflections:reflections:0.+")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.+")
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
            artifactId = "Lola-Core-Kotlin"
            version = "1.0-SNAPSHOT"

            from(components["kotlin"])
        }
    }
}