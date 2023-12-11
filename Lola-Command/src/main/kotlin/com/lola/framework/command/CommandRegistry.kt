package com.lola.framework.command

import com.lola.framework.core.decoration.ResolveClassListener
import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import com.lola.framework.core.decoration.ForAll
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

val parserFabrics: MutableList<ArgumentParserFabric<*>> = ArrayList()
val commands: MutableList<CommandClass> = ArrayList()
val parserAddListeners: MutableList<ArgumentReference> = ArrayList()

@ForAll
internal class CommandRegistry(override val target: Lola) : ResolveClassListener<Lola> {
    override fun <T : Any> onClassFound(clazz: LClass<T>) {
        if (clazz.self.isSubclassOf(ArgumentParserFabric::class) && !clazz.self.isAbstract &&
            clazz.self.constructors.any { it.parameters.isEmpty() }
        ) {
            val argParser = clazz.createInstance(emptyMap()) as ArgumentParserFabric<*>
            log.debug { "Found argument parser '$argParser'." }
            parserFabrics += argParser
            parserAddListeners.forEach { arg -> arg.onParserAdded(argParser) }
        }
        val ann = clazz.self.findAnnotation<Command>()
        if (ann != null) {
            if (!clazz.self.isSubclassOf(Runnable::class)) {
                log.error { "Command container should implement interface Runnable." }
                return
            }
            val decoration = CommandClass(clazz as LClass<Runnable>, ann)
            log.info { "Found $decoration." }
            clazz.decorate(decoration)
            commands += decoration
        }
    }
}