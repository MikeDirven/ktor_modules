package nl.icsvertex.ktor.modules.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar

abstract class CompileModules : DefaultTask() {
    @get:InputDirectory
    abstract val buildLocation: DirectoryProperty

    @TaskAction
    fun compile() {
        dependsOn(
            project.tasks.withType(Jar::class.java)
        )

        dependsOn(
            project.tasks.withType(CopyDependencies::class.java)
        )
    }

    companion object {
        fun registerCompileModulesTask(project: Project) {
            project.tasks.register("ktor.modules.compile", CompileModules::class.java)
        }
    }
}