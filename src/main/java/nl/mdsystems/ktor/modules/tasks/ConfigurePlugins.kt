package nl.mdsystems.ktor.modules.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task that configures and applies plugins to a project.
 *
 * This task is designed to be used as a companion object within a Gradle plugin.
 * It provides a way to add multiple plugins to a project in a single task execution.
 *
 * @property plugins A list of plugin names to be applied to the project.
 *
 * @see org.gradle.api.Project
 * @see org.gradle.api.tasks.TaskAction
 * @see org.gradle.api.provider.ListProperty
 * @see org.gradle.api.tasks.Input
 */
abstract class ConfigurePlugins : DefaultTask() {
    /**
     * A list of plugin names to be applied to the project.
     *
     * The list is represented as a [ListProperty] of [String] values.
     * Each string in the list should represent a plugin name,
     * which can be used to apply the corresponding plugin to the project.
     */
    @get:Input
    abstract var plugins: ListProperty<String>

    /**
     * The action to be performed when the task is executed.
     *
     * This action applies all the plugins specified in the [plugins] list to the project.
     * It uses the [project.afterEvaluate] method to ensure that the project has been fully configured before applying the plugins.
     */
    @TaskAction
    fun addDependencies() {
        project.afterEvaluate { 
            plugins.get().forEach { plugin ->
                project.pluginManager.apply(plugin)
            }
        }
    }

    companion object {
        /**
         * Registers a new instance of the [ConfigurePlugins] task with the given project.
         *
         * This method creates a new task named "addDependencies" of type [ConfigurePlugins].
         * It sets the initial list of plugins to be applied to the project using the [plugins.set] method.
         *
         * @param project The Gradle project to which the task will be added.
         */
        fun registerConfigurePluginsTask(project: Project){            
            project.tasks.register("addDependencies", ConfigurePlugins::class.java){
                it.plugins.set(
                    listOf(
                        "org.jetbrains.kotlin.jvm:2.0.0",
                        "io.ktor.plugin:2.3.10"
                    )
                )
            }
        }
    }
}