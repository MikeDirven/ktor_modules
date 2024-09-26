package nl.mdsystems.ktor.modules

import nl.mdsystems.ktor.modules.config.KtorModuleConfig
import nl.mdsystems.ktor.modules.extensions.*
import nl.mdsystems.ktor.modules.tasks.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar


/**
 * A Gradle plugin that provides a modular structure for a Ktor application.
 * This plugin sets up necessary configurations, tasks, and dependencies for a Ktor application.
 *
 * @author Mike Dirven
 */
abstract class KtorModules : Plugin<Project> {

    /**
     * Applies the KtorModules plugin to the given project.
     *
     * @param project The Gradle project to apply the plugin to.
     */
    override fun apply(project: Project) {
        // Create a configuration object for the plugin
        val config = project.extensions.create("ktorModule", KtorModuleConfig::class.java)

        // Add needed gradle plugins
        addPlugins(project)
        addRepositories(project)
        addDependencies(project)

        // Configure compile tasks
        CopyDependencies.registerCopyDependenciesTask(project)
        CompileModules.registerCompileModulesTask(project)

        // Setup jar task
        project.tasks.withType(Jar::class.java) { task ->
            task.doFirst {
                // Set the destination directory for the jar file
                task.destinationDirectory.set(config.buildLocation)

                // Configure the manifest of the jar file
                task.manifest { manifest ->
                    // Set the main class and version attributes in the manifest
                    manifest.attributes["Main-Class"] = config.mainClass
                    manifest.attributes["Version"] = project.version
                }
            }
        }
    }
}