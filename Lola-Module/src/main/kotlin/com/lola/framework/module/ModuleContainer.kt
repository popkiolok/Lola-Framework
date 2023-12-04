package com.lola.framework.module

import com.lola.framework.core.decoration.ForAnnotated
import com.lola.framework.core.LClass
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.LogWhenDecorate

@LogWhenDecorate("Lola-Module")
@ForAnnotated(Module::class)
class ModuleContainer<T : Any>(override val target: LClass<T>, val data: Module) : Decoration<LClass<T>> {
    val group: String
        get() = data.name.substringBefore(':')

    val simpleName: String
        get() = data.name.substringAfter(':')
}