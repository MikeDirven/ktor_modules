package nl.mdsystems.ktor.modules.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task that configures dependencies for a project.
 *
 * This task extends the [DefaultTask] class and is intended to be used within a Gradle build script.
 * It provides functionality to add a list of dependencies to the project's build configuration.
 *
 * @property dependencies A list of dependencies to be added to the project.
 *
 * @see DefaultTask
 */
abstract class ConfigureRepositories : DefaultTask() {

    /**
     * A list of dependencies to be added to the project.
     *
     * This property is annotated with `@get:Input` to indicate that its value will be used as an input
     * for the task execution.
     *
     * @see ListProperty
     */
    @get:Input
    abstract var dependencies: ListProperty<String>

    /**
     * The action to be performed when the task is executed.
     *
     * This method iterates over the list of dependencies and adds each one to the project's build
     * configuration using the `implementation` configuration.
     *
     * @see TaskAction
     */
    @TaskAction
    fun addDependencies() {
        dependencies.get().forEach { dependency ->
            project.dependencies.add("implementation", dependency)
        }
    }

    /**
     * A companion object that provides a factory method to register the [ConfigureRepositories] task.
     *
     * This method can be used within a Gradle build script to register the task and configure its
     * dependencies.
     *
     * @param project The Gradle project to which the task will be registered.
     *
     * @see Project.tasks.register
     */
    companion object {
        fun registerConfigureDependenciesTask(project: Project){
            project.tasks.register("addDependencies", ConfigureRepositories::class.java){
                it.dependencies.set(
                    listOf(
                        "nl.icsvertex.ktor:ics_core_server:0.0.2.62"
                    )
                )
            }
        }
    }
}