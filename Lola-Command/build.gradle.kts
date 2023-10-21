plugins {
    kotlin("jvm") version "1.9.0"
    id("me.champeau.jmh") version "0.6.8"
}

group = "com.lola.framework"
version = "1.0-SNAPSHOT"

sourceSets.create("example")

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.lola.framework:Lola-Core:1.0-SNAPSHOT")
    implementation("com.lola.framework:Lola-Module:1.0-SNAPSHOT")
    implementation("com.lola.framework:Lola-Setting:1.0-SNAPSHOT")

    implementation("io.github.oshai:kotlin-logging-jvm:5.+")
    implementation("org.slf4j:slf4j-api:2.+")
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.+")

    implementation("org.apache.commons:commons-text:1.+")

    testImplementation(kotlin("test"))
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.+")

    "exampleImplementation"(sourceSets.main.get().runtimeClasspath)
    "exampleImplementation"("com.lola.framework:Lola-Core-Kotlin:1.0-SNAPSHOT")
    "exampleImplementation"("org.apache.logging.log4j:log4j-slf4j2-impl:2.+")

    "jmhImplementation"(sourceSets.main.get().runtimeClasspath)
    "jmhImplementation"("com.lola.framework:Lola-Core-Kotlin:1.0-SNAPSHOT")
    "jmhImplementation"("org.apache.logging.log4j:log4j-slf4j2-impl:2.+")
    "jmhImplementation"("org.openjdk.jmh:jmh-core:+")
    "jmhImplementation"("org.openjdk.jmh:jmh-generator-annprocess:+")
    "jmhAnnotationProcessor"("org.openjdk.jmh:jmh-generator-annprocess:+")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}