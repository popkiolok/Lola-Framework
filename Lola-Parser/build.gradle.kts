plugins {
    kotlin("jvm") version "1.9.0"
}

group = "com.lola.framework"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.lola.framework:Lola-Core:1.0-SNAPSHOT")
    implementation("com.lola.framework:Lola-Module:1.0-SNAPSHOT")

    testImplementation(kotlin("test"))
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.+")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}