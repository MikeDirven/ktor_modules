package nl.icsvertex.ktor.modules

import nl.icsvertex.ktor.modules.config.KtorModulesConfig
import nl.icsvertex.ktor.modules.tasks.CompileModules
import nl.icsvertex.ktor.modules.tasks.CopyDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar

abstract class KtorModules : Plugin<Project> {
    private lateinit var config: KtorModulesConfig

    override fun apply(project: Project) {
        config = project.extensions.create("ktorModules", KtorModulesConfig::class.java)

        CopyDependencies.registerCopyDependenciesTask(project, config.buildLocation)
        CompileModules.registerCompileModulesTask(project)

        project.tasks.withType(Jar::class.java) { task ->
            if(!config.buildLocation.isDirectory) throw IllegalStateException("ktorModules.buildLocation need to be a directory!")
            task.destinationDirectory.set(config.buildLocation)
            task.manifest { manifest ->
                manifest.attributes["Main-Class"] = config.mainClass.ifBlank { throw IllegalStateException("Mainclass property cannot be empty!") }
                manifest.attributes["Version"] = project.version
            }
        }
    }
}