package com.lola.framework.module

import com.lola.framework.core.context.Context
import com.lola.framework.core.lola
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.cast

/**
 * Storage of module instances.
 */
class ModuleInstanceStorage(val ctxInitializer: (Context) -> Unit = {}) {
    /**
     * Returns collection of currently loaded modules in this [ModuleInstanceStorage].
     */
    val loaded: Collection<Any>
        get() = loadedMap.values

    val loadedMap: MutableMap<ModuleClass<*>, Any> = LinkedHashMap()

    var onUnload = { _: Any -> } // temporary

    /**
     * Creates new instance for [moduleClass] if the module is not loaded and returns this instance.
     */
    fun <T : Any> load(
        moduleClass: ModuleClass<T>,
        params: Map<KParameter, Any?> = emptyMap(),
        propertyValues: Map<KProperty<*>, Any?> = emptyMap()
    ): T {
        return loadedMap[moduleClass]?.let { moduleClass.target.self.cast(it) } ?: run {
            val inst = moduleClass.target.createInstance(params, propertyValues, ctxInitializer = { ctx ->
                ctx.register { this }
                ctxInitializer(ctx)
            })
            // Important to put instance before calling on load functions, because code executing in these functions
            // can call load function for this module another time, and if instance is not present in the map,
            // the module will be loaded twice.
            loadedMap[moduleClass] = inst
            moduleClass.target.getDecoratedMembers<OnLoadFunction>().forEach { it.target.self.call(inst) }
            log.info { "Loaded module '${moduleClass.data.name}'." }
            inst
        }
    }

    fun <T : Any> load(
        moduleClass: KClass<T>,
        params: Map<KParameter, Any?> = emptyMap(),
        propertyValues: Map<KProperty<*>, Any?> = emptyMap()
    ): T = load(moduleClass.lola.asModuleClass, params, propertyValues)

    @JvmOverloads
    fun <T : Any> load(
        moduleClass: Class<T>,
        params: Map<KParameter, Any?> = emptyMap(),
        propertyValues: Map<KProperty<*>, Any?> = emptyMap()
    ): T = load(moduleClass.kotlin, params, propertyValues)

    /**
     * Unloads a module from loaded module storage.
     */
    fun unload(moduleClass: ModuleClass<*>) {
        val inst = loadedMap.remove(moduleClass) ?: return
        onUnload(inst)
        moduleClass.target.getDecoratedMembers<OnUnloadFunction>().forEach { it.target.self.call(inst) }
        log.info { "Unloaded module '${moduleClass.data.name}'." }
    }

    /**
     * Gets if module is loaded in this storage.
     */
    fun isLoaded(moduleClass: ModuleClass<*>): Boolean {
        return loadedMap.containsKey(moduleClass)
    }
}

inline fun <T : Any> ModuleInstanceStorage.ifLoaded(moduleClass: ModuleClass<T>, action: (T) -> Unit) {
    loadedMap[moduleClass]?.let { action(moduleClass.target.self.cast(it)) }
}

inline fun <reified T : Any> ModuleInstanceStorage.ifLoaded(action: (T) -> Unit) {
    ifLoaded(T::class, action)
}

inline fun <T : Any> ModuleInstanceStorage.ifLoaded(moduleClass: KClass<T>, action: (T) -> Unit) {
    ifLoaded(moduleClass.lola.asModuleClass, action)
}