package com.lola.framework.module

import com.lola.framework.core.LClass
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.getDecoration
import com.lola.framework.core.decoration.hasDecoration

val <T : Any> LClass<T>.isModule: Boolean
    get() = hasDecoration<ModuleContainer<T>>()

val <T : Any> LClass<T>.asModule: ModuleContainer<T>
    get() = getDecoration<ModuleContainer<T>>()

val Context.mis: ModuleInstanceStorage
    get() = this[ModuleInstanceStorage::class]
        ?: throw NullPointerException("No ModuleInstanceStorage present in context '$this'.")