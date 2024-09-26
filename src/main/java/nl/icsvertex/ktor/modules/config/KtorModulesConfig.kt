package nl.icsvertex.ktor.modules.config

import java.io.File

class KtorModulesConfig {
    var mainClass: String = ""
    var buildLocation: File = File("build")
    var includeDependencies: Boolean = true
}