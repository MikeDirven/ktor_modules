package nl.mdsystems.ktor.modules.tasks

import nl.mdsystems.ktor.modules.config.KtorModuleConfig
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar

/**
 * A Gradle task that compiles Ktor modules.
 *
 * This task extends the DefaultTask class and is responsible for compiling the Ktor modules.
 * It checks the configuration provided by the [KtorModuleConfig] extension and performs necessary
 * actions such as creating the build location directory and validating the main class property.
 *
 * @see KtorModuleConfig
 */
abstract class CompileModules : DefaultTask() {
    /**
     * The main action of the task.
     *
     * This method retrieves the [KtorModuleConfig] extension from the project and performs the
     * following checks:
     * 1. Checks if the build location directory exists, and if not, creates it.
     * 2. Validates that the build location is a directory.
     * 3. Validates that the main class property is not blank.
     *
     * If any of the checks fail, an [IllegalStateException] is thrown with an appropriate error message.
     */
    @TaskAction
    fun compile() {
        val config = project.extensions.getByType(KtorModuleConfig::class.java)

        // Check configuration
        if(!config.buildLocation.exists()) config.buildLocation.mkdirs()
        if(!config.buildLocation.isDirectory) throw IllegalStateException("ktorModules.buildLocation need to be a directory!")
        if(config.mainClass.isBlank()) throw IllegalStateException("Mainclass property cannot be empty!")
    }
    
    companion object {
        /**
         * Registers the "compile" task for the given project.
         *
         * This method creates a "compile" task of type [CompileModules] and configures it to depend
         * on the "copyDependencies" and "jar" tasks. It also sets the task group to "ktor Modules".
         *
         * @param project The Gradle project to register the task for.
         */
        fun registerCompileModulesTask(project: Project) {
            project.tasks.register("compile", CompileModules::class.java){
                val config = project.extensions.getByType(KtorModuleConfig::class.java)
                it.group = "ktor Modules"

                it.dependsOn(
                    project.tasks.withType(CopyDependencies::class.java)
                )

                it.dependsOn(
                    project.tasks.withType(Jar::class.java)
                )
            }
        }
    }
}