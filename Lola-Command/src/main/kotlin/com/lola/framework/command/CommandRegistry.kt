package com.lola.framework.command

import com.lola.framework.core.annotation.findAnnotation
import com.lola.framework.core.container.AddContainerListener
import com.lola.framework.core.container.Container
import com.lola.framework.core.container.subscribeAddContainerListener
import kotlin.reflect.full.isSubclassOf

object CommandRegistry : AddContainerListener {
    val parserFabrics: MutableList<ArgumentParserFabric<*>> = ArrayList()
    val commands: MutableList<CommandContainer> = ArrayList()
    val parserAddListeners: MutableList<ArgumentProperty> = ArrayList()

    init {
        subscribeAddContainerListener(this)
    }

    override fun onContainerAdded(container: Container) {
        if (container.clazz.isSubclassOf(ArgumentParserFabric::class) && !container.clazz.isAbstract && container.hasDefaultConstructor()) {
            val argParser = container.createInstance(emptyMap()).instance as ArgumentParserFabric<*>
            log.debug { "Found argument parser $argParser." }
            parserFabrics += argParser
            parserAddListeners.forEach { arg -> arg.onParserAdded(argParser) }
        }
        val ann = container.findAnnotation<Command>()
        if (ann != null) {
            if (!container.clazz.isSubclassOf(Runnable::class)) {
                log.error { "Command container should implement interface Runnable." }
                return
            }
            val decoration = CommandContainer(ann.name, container)
            log.info { "Found command $decoration." }
            container.decorate(decoration)
            commands += decoration
        }
    }
}