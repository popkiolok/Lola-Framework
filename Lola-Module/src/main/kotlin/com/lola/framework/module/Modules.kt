package com.lola.framework.module

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