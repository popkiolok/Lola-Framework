package com.lola.framework.module

import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import com.lola.framework.core.container.subscribeAddContainerListener
import com.lola.framework.core.decoration.DecorateClassListener
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.kotlin.LClassKotlin
import com.lola.framework.core.kotlin.getKotlinContainer
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class ModuleRegistry(override val target: Lola) : DecorateClassListener {
    /**
     * Modules associated by their [ModuleContainer.group]s.
     */
    val modulesByGroup: Map<String, Collection<ModuleContainer<*>>> = HashMap()

    /**
     * Unmodifiable map of modules associated by names, where name is `group:path` string.
     */
    val modulesByName: Map<String, ModuleContainer<*>> = HashMap()

    init {
        subscribeAddContainerListener(this)
    }

    /**
     * Gets module by it name from [modulesByName] map.
     *
     * @param name The module [ModuleContainer.group]:[ModuleContainer.path] string.
     */
    operator fun get(name: String): ModuleContainer<*> {
        return modulesByName[name] ?: throw NullPointerException("Module with name $name does not exist.")
    }

    operator fun <T : Any> get(clazz: KClass<T>): ModuleContainer<T> {
        return (getKotlinContainer(clazz) ?: LClassKotlin(clazz)).asModule
    }

    override fun <T : Any> onDecoratedClass(clazz: LClass<T>, decoration: Decoration<LClass<T>>) {
        TODO("Not yet implemented")
    }

    override fun onClassFound(clazz: LClass<*>) {
        clazz.findAnnotation<Module>()?.let { ann ->
            ((modulesByGroup as HashMap).computeIfAbsent(ann.group) { ArrayList() } as ArrayList).add(ann.group, ann)
            (modulesByName as HashMap)["${ann.group}:${ann.path}"] = mc
            log.info { "Resolved module '${mc.name}'." }
        }
    }
}