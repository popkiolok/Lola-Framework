package com.lola.framework.module

import com.lola.framework.core.LClass
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.ForAnnotated
import com.lola.framework.core.decoration.LogWhenDecorate

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Module(val name: String = "", val info: String = "")

val Module.group: String
    get() = name.substringBefore(':')

val Module.simpleName: String
    get() = name.substringAfter(':')

@LogWhenDecorate(logger = "Lola-Module", pattern = "Found {decoration}")
@ForAnnotated(Module::class)
class ModuleClass<T : Any>(override val target: LClass<T>, val data: Module) : Decoration<LClass<T>> {
    init {
        ((modulesByGroup as HashMap).computeIfAbsent(data.group) { ArrayList() } as ArrayList).add(this)
        (modulesByName as HashMap)[data.name] = this
    }

    override fun toString(): String {
        return "module ${data.name}"
    }
}