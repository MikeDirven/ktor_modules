package nl.mdsystems.ktor.modules

import nl.mdsystems.ktor.modules.config.KtorModulesConfig
import nl.mdsystems.ktor.modules.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

/**
 * A Gradle plugin that provides functionality for managing and building modular Ktor applications.
 *
 * This plugin creates tasks for copying dependencies, compiling modules, and configuring the JAR manifest.
 *
 * @author [Your Name]
 */
abstract class KtorModules : Plugin<Project> {

    /**
     * The configuration object for the KtorModules plugin.
     *
     * This object holds properties such as the build location, main class, and other configurations.
     */
    private lateinit var config: KtorModulesConfig

    /**
     * Applies the KtorModules plugin to the given project.
     *
     * This method initializes the configuration object, registers tasks for copying dependencies, compiling modules,
     * and configuring the JAR manifest.
     *
     * @param project The Gradle project to which the plugin will be applied.
     */
    override fun apply(project: Project) {
        config = project.extensions.create("ktorModules", KtorModulesConfig::class.java)

        // Configure main tasks
        ConfigurePlugins.registerConfigurePluginsTask(project)
        ConfigureRepositories.registerConfigureDependenciesTask(project)
        ConfigureDependencies.registerAddDependenciesTask(project)

        // Configure compile tasks
        CopyDependencies.registerCopyDependenciesTask(project, config.buildLocation)
        CompileModules.registerCompileModulesTask(project)

        // Configure jar task
        project.tasks.withType(Jar::class.java) { task ->
            if(!config.buildLocation.isDirectory) throw IllegalStateException("ktorModules.buildLocation need to be a directory!")
            task.destinationDirectory.set(config.buildLocation)
            task.manifest { manifest ->
                manifest.attributes["Main-Class"] = config.mainClass.ifBlank {
                    throw IllegalStateException("Mainclass property cannot be empty!")
                }
                manifest.attributes["Version"] = project.version
            }
        }
    }
}