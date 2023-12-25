package com.lola.framework.module

import com.lola.framework.core.lola
import com.lola.framework.core.LClass
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.getDecoration
import com.lola.framework.core.decoration.hasDecoration
import kotlin.reflect.KClass

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
fun getModuleClass(name: String): ModuleClass<*> {
    return modulesByName[name] ?: throw NullPointerException("Module with name '$name' does not exist.")
}

val <T : Any> LClass<T>.isModuleClass: Boolean
    get() = hasDecoration<ModuleClass<T>>()

val <T : Any> LClass<T>.asModuleClass: ModuleClass<T>
    get() = getDecoration<ModuleClass<T>>()

val Context.mis: ModuleInstanceStorage
    get() = this[ModuleInstanceStorage::class]
        ?: throw NullPointerException("No ModuleInstanceStorage present in context '$this'.")

fun <T : Any> Context.getModule(moduleClass: ModuleClass<T>) =
    moduleInstanceSuppliers.firstNotNullOf { it.get(moduleClass, this) }

fun <T : Any> Context.getModule(moduleClass: LClass<T>) = getModule(moduleClass.asModuleClass)

fun <T : Any> Context.getModule(moduleClass: KClass<T>) = getModule(moduleClass.lola)

fun <T : Any> Context.getModule(moduleClass: Class<T>) = getModule(moduleClass.kotlin)