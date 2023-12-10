package com.lola.framework.module

import com.lola.framework.core.context.Context

class FromStorageSupplier : ModuleInstanceSupplier {
    override val priority: Int
        get() = 0

    override fun <T : Any> get(moduleClass: ModuleClass<T>, context: Context): T {
        return context.mis.load(moduleClass)
    }
}