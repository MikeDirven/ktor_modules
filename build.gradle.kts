plugins {
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "1.2.1"
    kotlin("jvm") version "2.0.20"
}

group = "nl.mdsystems.ktor"
version = "0.0.1"

gradlePlugin {
    plugins {
        create("ktor-modules") {
            id = "org.example.greeting"
            implementationClass = "nl.icsvertex.ktor.modules.KtorModules"
        }
    }
}

gradlePlugin {
    website = "<substitute your project website>"
    vcsUrl = "<uri to project source repository>"

    // ...
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