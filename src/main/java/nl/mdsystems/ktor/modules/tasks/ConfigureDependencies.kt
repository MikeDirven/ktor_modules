package nl.mdsystems.ktor.modules.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task that configures dependencies for a project.
 *
 * This task extends the DefaultTask class and is designed to be used within a Gradle build script.
 * It provides functionality to add a list of dependencies to a project's build configuration.
 *
 * @author [Your Name]
 */
abstract class ConfigureDependencies : DefaultTask() {
    /**
     * A list of dependencies to be added to the project.
     *
     * This property is annotated with `@get:Input` to indicate that its value will be used as an input
     * for the task execution. The dependencies are expected to be provided as a list of strings.
     */
    @get:Input
    abstract var dependencies: ListProperty<String>

    /**
     * The action to be performed by the task.
     *
     * This method iterates over the list of dependencies and adds each one to the project's build
     * configuration using the `implementation` configuration.
     */
    @TaskAction
    fun addDependencies() {
        dependencies.get().forEach { dependency ->
            project.dependencies.add("implementation", dependency)
        }
    }

    companion object {
        /**
         * Registers an instance of the [ConfigureDependencies] task with the given project.
         *
         * This method creates a new instance of the task and configures it with a default list of
         * dependencies. The task is then registered with the provided project under the name
         * "addDependencies".
         *
         * @param project The Gradle project to register the task with.
         */
        fun registerAddDependenciesTask(project: Project){
            project.tasks.register("addDependencies", ConfigureDependencies::class.java){
                it.dependencies.set(
                    listOf(
                        "nl.icsvertex.ktor:ics_core_server:0.0.2.62"
                    )
                )
            }
        }
    }
}