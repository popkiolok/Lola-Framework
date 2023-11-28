package com.lola.framework.module

import com.lola.framework.core.LClass
import com.lola.framework.core.container.context.Context
import com.lola.framework.core.decoration.hasDecoration
import com.lola.framework.core.kotlin.lolaClass

val <T : Any> LClass<T>.isModule: Boolean
    get() = hasDecoration<ModuleContainer<T>>()

val <T : Any> LClass<T>.asModule: ModuleContainer<T>
    get() = getDecoration<ModuleContainer<T>>() ?: throw NullPointerException("Container '$this' is not a module.")

val <T : Any> T.asModule: ModuleContainer<T>
    get() = lolaClass.getDecoration<ModuleContainer<T>>()
        ?: throw NullPointerException("Container '$lolaClass' is not a module.")

val Context.mis: ModuleInstanceStorage
    get() = this[ModuleInstanceStorage::class] as ModuleInstanceStorage?
        ?: throw NullPointerException("No ModuleInstanceStorage present in context '$this'.")