package nl.icsvertex.ktor.processor.visitors

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.ClassKind
import java.io.OutputStream

class KtorModuleVisitor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val ktorControllerClasses: Map<KSClassDeclaration, String>,
    private val ktorServiceClasses: List<KSClassDeclaration>,
    private val ktorScheduleClasses: List<KSClassDeclaration>
) : KSVisitorVoid() {

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        val moduleFunction = function.simpleName.asString()
        val packageName = function.packageName.asString()
        val fileName = "${moduleFunction}Generated"

        fun getInvocation(clazz: KSClassDeclaration): String {
            val fqdn = clazz.qualifiedName?.asString() ?: clazz.simpleName.asString()

            // Check 1: De standaard KSP check
            val isStandardObject = clazz.classKind == ClassKind.OBJECT

            // Check 2: De bytecode fallback (kijken of er een 'INSTANCE' veld of 'Companion' in zit)
            val hasObjectInstance = clazz.declarations.any {
                it.simpleName.asString() == "INSTANCE"
            }

            // Check 3: Controleer of de tekstuele representatie van KSP het stiekem een object noemt
            val isTextObject = clazz.toString().contains("object", ignoreCase = true)

            val isActuallyAnObject = isStandardObject || hasObjectInstance || isTextObject

            return if (isActuallyAnObject) fqdn else "$fqdn()"
        }

        val controllerInits = ktorControllerClasses.map { (controller, path) ->
            val invocation = getInvocation(controller)
            if (path.isBlank()) "this init $invocation" else "this path \"$path\" init $invocation"
        }

        val serviceInits = ktorServiceClasses.map { service ->
            "this add ${getInvocation(service)}"
        }

        val scheduleInits = ktorScheduleClasses.map { schedule ->
            "this schedule ${getInvocation(schedule)}"
        }

        val file: OutputStream = codeGenerator.createNewFile(
            dependencies = Dependencies(
                true,
                function.containingFile!!,
                *ktorControllerClasses.map { it.key.containingFile!! }.toTypedArray(),
                *ktorServiceClasses.map { it.containingFile!! }.toTypedArray(),
                *ktorScheduleClasses.map { it.containingFile!! }.toTypedArray()
            ),
            packageName = packageName,
            fileName = fileName
        )

        file.write("package $packageName\n\n".toByteArray())
        file.write("import nl.icsvertex.server.controllers.controllers\n".toByteArray())
        file.write("import nl.icsvertex.server.services.services\n".toByteArray())
        file.write("import nl.icsvertex.server.schedules.schedules\n".toByteArray())
        file.write("import nl.icsvertex.server.modules.ktorModule\n".toByteArray())
        file.write("import nl.icsvertex.server.modules.types.KtorModule\n\n".toByteArray())
        file.write(
            """
            fun KM_$moduleFunction(): KtorModule = ktorModule {                
                controllers {
                    ${controllerInits.joinToString("\n")}
                }
                
                services {
                    ${serviceInits.joinToString("\n")}
                }
                
                schedules {
                    ${scheduleInits.joinToString("\n")}
                }
                
                
                $moduleFunction()
            }
        """.trimIndent().toByteArray())
        file.close()
    }
}