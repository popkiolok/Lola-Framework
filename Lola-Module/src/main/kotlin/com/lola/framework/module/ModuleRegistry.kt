package com.lola.framework.module

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import com.lola.framework.core.container.Container
import com.lola.framework.core.container.AddContainerListener
import com.lola.framework.core.container.subscribeAddContainerListener
import com.lola.framework.core.kotlin.KotlinContainer
import com.lola.framework.core.kotlin.getKotlinContainer
import kotlin.reflect.KClass

object ModuleRegistry : AddContainerListener {
    val modules: Collection<ModuleContainer>
        get() = modules0

    /**
     * Unmodifiable [Multimap] of modules associated by their [ModuleContainer.group]s.
     */
    val modulesByGroup: Multimap<String, ModuleContainer>
        get() = Multimaps.unmodifiableMultimap(modulesByGroup0)

    /**
     * Unmodifiable map of modules associated by names, where name is group:pathString string.
     */
    val modulesByName: Map<String, ModuleContainer>
        get() = modulesByName0

    private val modules0 = ArrayList<ModuleContainer>()
    private val modulesByGroup0 = ArrayListMultimap.create<String, ModuleContainer>()
    private val modulesByName0: MutableMap<String, ModuleContainer> = HashMap()

    init {
        subscribeAddContainerListener(this)
    }

    /**
     * Gets module by it name from [modulesByName] map.
     *
     * @param name The module [ModuleContainer.group]:[ModuleContainer.path] string.
     */
    operator fun get(name: String): ModuleContainer {
        return modulesByName[name] ?: throw NullPointerException("Module with name $name does not exist.")
    }

    operator fun <T : Any> get(clazz: KClass<T>): ModuleContainer {
        return (getKotlinContainer(clazz) ?: KotlinContainer(clazz)).asModule
    }

    override fun onContainerAdded(container: Container) {
        if (container.annotations.hasAnnotation(Module::class)) {
            val mc = ModuleContainer(container)
            container.decorate(mc)
            modules0 += mc
            modulesByGroup0.put(mc.group, mc)
            modulesByName0[mc.name] = mc
            log.info { "Resolved module '${mc.name}'." }
        }
    }
}