package com.lola.framework.core.decoration

import com.lola.framework.core.LClass
import com.lola.framework.core.context.Context

interface CreateInstanceListener<T : Any> : Decoration<LClass<T>> {
    fun onCreateInstance(instance: T, context: Context)
}