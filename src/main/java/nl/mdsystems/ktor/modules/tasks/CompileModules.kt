package nl.mdsystems.ktor.modules.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar

/**
 * A Gradle task that compiles Ktor modules.
 *
 * This task is responsible for compiling the Ktor modules and ensuring that the necessary dependencies are available.
 * It depends on the [Jar] and [CopyDependencies] tasks to perform its actions.
 *
 * @property buildLocation The directory where the compiled modules will be located.
 *
 * @see Jar
 * @see CopyDependencies
 */
abstract class CompileModules : DefaultTask() {
    /**
     * The directory where the compiled modules will be located.
     *
     * This property is annotated with `@get:InputDirectory` to indicate that it is an input directory for the task.
     */
    @get:InputDirectory
    abstract val buildLocation: DirectoryProperty

    /**
     * The action to be performed by the task.
     *
     * This method sets up the dependencies for the task, ensuring that it runs after the [Jar] and [CopyDependencies] tasks.
     */
    @TaskAction
    fun compile() {
        dependsOn(
            project.tasks.withType(Jar::class.java)
        )

        dependsOn(
            project.tasks.withType(CopyDependencies::class.java)
        )
    }

    /**
     * A companion object that contains a method to register the [CompileModules] task.
     */
    companion object {
        /**
         * Registers the [CompileModules] task with the given project.
         *
         * @param project The Gradle project to register the task with.
         */
        fun registerCompileModulesTask(project: Project) {
            project.tasks.register("ktor.modules.compile", CompileModules::class.java)
        }
    }
}