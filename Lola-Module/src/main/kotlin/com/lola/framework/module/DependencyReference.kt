package com.lola.framework.module

import com.lola.framework.core.LAnnotatedElement
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.ForAnnotated
import com.lola.framework.core.decoration.ValueSupplier
import com.lola.framework.core.refType
import kotlin.reflect.jvm.jvmErasure

@ForAnnotated(Dep::class)
class DependencyReference(override val target: LAnnotatedElement, ann: Dep) :
    ValueSupplier<LAnnotatedElement, Any?> {
    private val module: ModuleContainer<*> by lazy {
        if (ann.name.isEmpty()) {
            ModuleRegistry[target.self.refType.jvmErasure]
        } else ModuleRegistry[ann.name]
    }

    override fun supplyValue(context: Context): Any {
        return try {
            (context[ModuleInstanceStorage::class] as ModuleInstanceStorage).load(module).instance
        } catch (e: Throwable) {
            log.error { "An error occurred while supplying dependency for '$target': '${e.message}'." }
            e.printStackTrace()
        }
    }
}