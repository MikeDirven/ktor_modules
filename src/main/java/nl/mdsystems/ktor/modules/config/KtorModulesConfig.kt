package nl.mdsystems.ktor.modules.config

import java.io.File

/**
 * This class is used to configure settings for the Ktor Modules system.
 *
 * @property mainClass The fully qualified name of the main class to be executed.
 * @property buildLocation The directory where the build artifacts are located. Default is "build".
 * @property includeDependencies A flag indicating whether dependencies should be included in the generated module. Default is true.
 */
class KtorModulesConfig {
    var mainClass: String = ""
    var buildLocation: File = File("build")
    var includeDependencies: Boolean = true
}