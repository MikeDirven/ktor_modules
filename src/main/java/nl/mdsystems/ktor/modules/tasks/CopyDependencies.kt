package nl.mdsystems.ktor.modules.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * A Gradle task that copies the runtime dependencies of a project to a specified directory.
 *
 * This task is designed to be used as a part of a larger build process for a Ktor application.
 * It extends the DefaultTask class and is intended to be registered as a custom task within a Gradle project.
 *
 * @property buildLocation The directory where the runtime dependencies will be copied to.
 * This property is annotated with `@InputDirectory` to indicate that it is an input to the task.
 *
 * @see DefaultTask
 * @see Project
 * @see DirectoryProperty
 * @see InputDirectory
 * @see TaskAction
 */
abstract class CopyDependencies : DefaultTask() {
    @get:InputDirectory
    abstract val buildLocation: DirectoryProperty

    /**
     * The main action of the task.
     *
     * This method copies the runtime dependencies of the project to the specified `buildLocation` directory.
     * It uses the `project.copy` function to perform the copying operation.
     *
     * @see Project.copy
     * @see configurations.getByName
     * @see File
     */
    @TaskAction
    fun copyDependencies() {
        project.copy {
            it.from(project.configurations.getByName("runtimeClasspath"))
            it.into(File(buildLocation.asFile.get(), "dependencies"))
        }
    }

    /**
     * A companion object that contains a factory method for registering the task.
     *
     * This method registers a new instance of the `CopyDependencies` task with the given `project` and `buildLocation`.
     * The task is registered with the name "ktor.modules.copy" and is of type `CopyDependencies`.
     *
     * @param project The Gradle project where the task will be registered.
     * @param buildLocation The directory where the runtime dependencies will be copied to.
     *
     * @see Project.tasks.register
     * @see CopyDependencies
     */
    companion object {
        fun registerCopyDependenciesTask(project: Project, buildLocation: File) {
            project.tasks.register("ktor.modules.copy", CopyDependencies::class.java){
                it.buildLocation.set(buildLocation)
            }
        }
    }
}