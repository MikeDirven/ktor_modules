package nl.mdsystems.ktor.modules.tasks

import nl.mdsystems.ktor.modules.config.KtorModuleConfig
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * A Gradle task that copies dependencies from the runtime classpath to a specified location.
 *
 * This task is intended to be used within a Gradle project that utilizes the Ktor framework.
 * It is registered as a custom task named "copyDependencies" and belongs to the "ktor Modules" group.
 *
 * @see CopyDependencies.copyDependencies
 * @see CopyDependencies.registerCopyDependenciesTask
 */
abstract class CopyDependencies : DefaultTask() {
    /**
     * The main action of the task.
     *
     * This function retrieves the [KtorModuleConfig] extension from the project and checks if the
     * `includeDependencies` flag is set to true. If it is, it copies all dependencies from the
     * "runtimeClasspath" configuration to a specified location within the project's build directory.
     *
     * @see KtorModuleConfig
     */
    @TaskAction
    fun copyDependencies() {
        val config = project.extensions.getByType(KtorModuleConfig::class.java)

        if(config.includeDependencies) {
            project.copy {
                it.from(project.configurations.getByName("runtimeClasspath"))
                it.into(File(config.buildLocation, "dependencies"))
            }
        }
    }

    /**
     * A companion object that contains a function to register the "copyDependencies" task.
     *
     * This function registers the custom task with the provided project, sets its group to "ktor Modules",
     * and associates it with the [CopyDependencies] class.
     *
     * @param project The Gradle project to which the task will be registered.
     * @see CopyDependencies
     */
    companion object {
        fun registerCopyDependenciesTask(project: Project) {
            project.tasks.register("copyDependencies", CopyDependencies::class.java){
                it.group = "ktor Modules"
            }
        }
    }
}