package com.lola.framework.command

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.lola.framework.core.container.AddContainerListener
import com.lola.framework.core.container.Container
import com.lola.framework.core.container.subscribeAddContainerListener
import com.lola.framework.core.toJSON
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

object CommandRegistry : AddContainerListener {
    val argumentParsers: MutableList<ArgumentParser> = ArrayList()
    val dynamicParsers: MutableList<Container> = ArrayList()
    val commands: MutableList<CommandContainer> = ArrayList()

    init {
        subscribeAddContainerListener(this)
    }

    override fun onContainerAdded(container: Container) {
        if (container.clazz.isSubclassOf(ArgumentParser::class) && !container.clazz.isAbstract) {
            if (container.hasDefaultConstructor()) {
                val argParser = container.createInstance(emptyMap()).instance as ArgumentParser
                log.debug { "Found argument parser $argParser." }
                argumentParsers += argParser
                commands.forEach {
                    it.arguments.forEach { arg -> arg.onParserAdded(argParser) }
                }
            }
        }
        val ann = container.annotations.findAnnotation(Command::class)
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