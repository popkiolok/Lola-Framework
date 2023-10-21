pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "Lola-Event"

includeBuild("../Lola-Core")
includeBuild("../Lola-Core-Kotlin")

includeBuild("../Lola-Module")
includeBuild("../Lola-Setting")