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
    override fun toString(): String {
        return "module ${data.name}"
    }
}