package com.lola.framework.module

import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import com.lola.framework.core.decoration.DecorateClassListener
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.ForAll

/**
 * Modules associated by their [Module.group]s.
 */
val modulesByGroup: Map<String, Collection<ModuleClass<*>>> = HashMap()

/**
 * Unmodifiable map of modules associated by names, where name is `group:simpleName` string.
 */
val modulesByName: Map<String, ModuleClass<*>> = HashMap()

/**
 * Gets module by it name from [modulesByName] map.
 *
 * @param name The module `group:simpleName` string.
 */
fun getModuleByName(name: String): ModuleClass<*> {
    return modulesByName[name] ?: throw NullPointerException("Module with name '$name' does not exist.")
}

@ForAll
internal class ModuleResolver(override val target: Lola) : DecorateClassListener<Lola> {
    override fun <T : Any> onDecoratedClass(decoration: Decoration<LClass<T>>) {
        if (decoration is ModuleClass) {
            ((modulesByGroup as HashMap).computeIfAbsent(decoration.data.group) { ArrayList() } as ArrayList)
                .add(decoration)
            (modulesByName as HashMap)[decoration.data.name] = decoration
        }
    }
}