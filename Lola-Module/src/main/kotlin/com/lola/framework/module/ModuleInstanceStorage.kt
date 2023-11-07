package com.lola.framework.module

import com.lola.framework.core.container.Container
import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.container.context.Context
import com.lola.framework.core.decoration.hasDecoration
import com.lola.framework.core.function.parameter.Parameter
import kotlin.reflect.KClass

/**
 * Storage of module instances.
 */
class ModuleInstanceStorage(val ctxInitializer: (Context) -> Unit = {}) {
    /**
     * Returns unmodifiable view collection of currently loaded modules in this [ModuleInstanceStorage].
     */
    val loaded: Collection<ContainerInstance>
        get() = loadedMap.values

    val loadedMap: MutableMap<Container, ContainerInstance> = LinkedHashMap()

    /**
     * Creates new instance for container, if module is not loaded and returns this instance.
     *
     * @param module The [ModuleContainer] decoration of container to load.
     * @param params Parameters for container constructor.
     * @return The loaded container instance.
     */
    fun load(module: ModuleContainer, params: Map<Parameter, Any?> = emptyMap()): ContainerInstance {
        val container = module.self
        return loadedMap.getOrPut(container) {
            val inst = container.createInstance(
                params,
                ctxInitializer = { ctx ->
                    ctx.register(
                        "ModuleInstanceStorage",
                        ModuleInstanceStorage::class,
                        ModuleInstanceStorage::class.java
                    ) { this }
                    ctxInitializer(ctx)
                })
            container.allFunctions.forEach {
                if (it.hasDecoration<OnLoadFunction>()) {
                    it(inst)
                }
            }
            log.info { "Loaded module '${module.name}'." }
            inst
        }
    }

    /**
     * Unloads a module from the loaded module storage.
     *
     * @param module The [ModuleContainer] decoration of container to unload.
     */
    fun unload(module: ModuleContainer) {
        val container = module.self
        val inst = loadedMap.remove(container) ?: return
        container.allFunctions.forEach {
            if (it.hasDecoration<OnUnloadFunction>()) {
                it(inst)
            }
        }
        log.info { "Unloaded module '${module.name}'." }
    }

    /**
     * Gets if module is loaded in this storage.
     *
     * @param module The [ModuleContainer] decoration of container to check.
     * @return True if module is loaded, false otherwise.
     */
    fun isLoaded(module: ModuleContainer): Boolean {
        return loadedMap.containsKey(module.self)
    }
}

inline fun ModuleInstanceStorage.ifLoaded(module: ModuleContainer, action: (ContainerInstance) -> Unit) {
    loadedMap[module.self]?.let(action)
}

inline fun <reified T : Any> ModuleInstanceStorage.ifLoaded(module: KClass<T>, action: (T) -> Unit) {
    ifLoaded(ModuleRegistry[module]) { action(it.instance as T) }
}