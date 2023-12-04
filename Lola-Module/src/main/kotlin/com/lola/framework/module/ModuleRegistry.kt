package com.lola.framework.module

import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import com.lola.framework.core.container.subscribeAddContainerListener
import com.lola.framework.core.decoration.DecorateClassListener
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.ForAll
import com.lola.framework.core.kotlin.LClassKotlin
import com.lola.framework.core.kotlin.getKotlinContainer
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

@ForAll
class ModuleRegistry(override val target: Lola) : DecorateClassListener<Lola> {
    /**
     * Modules associated by their [ModuleContainer.group]s.
     */
    val modulesByGroup: Map<String, Collection<ModuleContainer<*>>> = HashMap()

    /**
     * Unmodifiable map of modules associated by names, where name is `group:simpleName` string.
     */
    val modulesByName: Map<String, ModuleContainer<*>> = HashMap()

    /**
     * Gets module by it name from [modulesByName] map.
     *
     * @param name The module `group:simpleName` string.
     */
    operator fun get(name: String): ModuleContainer<*> {
        return modulesByName[name] ?: throw NullPointerException("Module with name '$name' does not exist.")
    }

    operator fun <T : Any> get(clazz: KClass<T>): ModuleContainer<T> {
        return (getKotlinContainer(clazz) ?: LClassKotlin(clazz)).asModule
    }

    override fun <T : Any> onDecoratedClass(decoration: Decoration<LClass<T>>) {
        ((modulesByGroup as HashMap).computeIfAbsent(ann.group) { ArrayList() } as ArrayList).add(ann.group, ann)
        (modulesByName as HashMap)["${ann.group}:${ann.path}"] = mc
    }
}