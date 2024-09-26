plugins {
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "1.2.1"
    kotlin("jvm") version "2.0.20"
}

group = "nl.mdsystems.ktor"
version = "0.0.1"

gradlePlugin {
    website = "https://github.com/MikeDirven/ktor_modules"
    vcsUrl = "https://github.com/MikeDirven/ktor_modules"

    plugins {
        create("ktor-modules") {
            id = "nl.mdsystems.ktor.modules"
            displayName = "Gradle plugin for ktor modules system"
            description = "Gradle plugin to help out building the ktor modules, that have been build with the ktor modules implementation"
            tags = listOf("ktor", "modules", "jetbrains", "plugins")
            implementationClass = "nl.mdsystems.ktor.modules.KtorModules"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}