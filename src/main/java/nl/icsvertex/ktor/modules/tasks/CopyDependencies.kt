package nl.icsvertex.ktor.modules.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class CopyDependencies : DefaultTask() {
    @get:InputDirectory
    abstract val buildLocation: DirectoryProperty

    @TaskAction
    fun copyDependencies() {
        project.copy {
            it.from(project.configurations.getByName("runtimeClasspath"))
            it.into(File(buildLocation.asFile.get(), "dependencies"))
        }
    }

    companion object {
        fun registerCopyDependenciesTask(project: Project, buildLocation: File) {
            project.tasks.register("ktor.modules.copy", CopyDependencies::class.java){
                it.buildLocation.set(buildLocation)
            }
        }
    }
}