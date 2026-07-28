package nl.icsvertex.ktor.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import nl.icsvertex.ktor.processor.visitors.KtorModuleVisitor
import nl.icsvertex.server.controllers.annotations.KtorController
import nl.icsvertex.server.modules.annotations.KtorModule
import nl.icsvertex.server.schedules.annotations.KtorSchedule
import nl.icsvertex.server.services.annotations.KtorService

class KtorModuleProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    // Gebruik Sets om duplicaten over meerdere KSP-rondes te voorkomen
    private val ktorControllerClasses = mutableMapOf<KSClassDeclaration, String>()
    private val ktorServiceClasses = mutableListOf<KSClassDeclaration>()
    private val ktorScheduleClasses = mutableListOf<KSClassDeclaration>()
    private var ktorModuleFunction: KSFunctionDeclaration? = null

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // 1. Zoek de KtorModule functie
        val moduleSymbols = resolver.getSymbolsWithAnnotation(KtorModule::class.qualifiedName!!)
        moduleSymbols.filterIsInstance<KSFunctionDeclaration>().forEach {
            ktorModuleFunction = it
        }

        // 2. Zoek alle schedules (Vindt nu ALTIJD objecten en klassen, ook nested!)
        val scheduleSymbols = resolver.getSymbolsWithAnnotation(KtorSchedule::class.qualifiedName!!)
        scheduleSymbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            if (!ktorScheduleClasses.contains(clazz)) {
                ktorScheduleClasses.add(clazz)
            }
        }

        // 3. Zoek alle controllers
        val controllerSymbols = resolver.getSymbolsWithAnnotation(KtorController::class.qualifiedName!!)
        controllerSymbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            val annotation = clazz.annotations.firstOrNull { it.shortName.asString() == "KtorController" }
            val path = annotation?.arguments?.find { it.name?.asString() == "path" }?.value as? String ?: ""
            ktorControllerClasses[clazz] = path
        }

        // 4. Zoek alle services
        val serviceSymbols = resolver.getSymbolsWithAnnotation(KtorService::class.qualifiedName!!)
        serviceSymbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            if (!ktorServiceClasses.contains(clazz)) {
                ktorServiceClasses.add(clazz)
            }
        }

        // 5. Genereer DIRECT zodra we de module functie hebben gevonden en de bestanden verwerkt zijn
        // We doen dit aan het einde van de ronde waarin de functie beschikbaar is
        ktorModuleFunction?.let { function ->
            function.accept(
                KtorModuleVisitor(codeGenerator, logger, ktorControllerClasses, ktorServiceClasses, ktorScheduleClasses),
                Unit
            )
            // Zet hem op null zodat we hem in een eventuele volgende ronde niet nóg een keer genereren
            ktorModuleFunction = null
        }

        return emptyList()
    }
}