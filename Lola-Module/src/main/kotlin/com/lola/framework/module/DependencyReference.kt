package com.lola.framework.module

import com.lola.framework.core.LAnnotatedElement
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.ForAnnotated
import com.lola.framework.core.decoration.ValueSupplier
import com.lola.framework.core.lola
import com.lola.framework.core.refType
import kotlin.reflect.jvm.jvmErasure

@ForAnnotated(Dep::class)
class DependencyReference(override val target: LAnnotatedElement, ann: Dep) : ValueSupplier<LAnnotatedElement, Any?> {
    private val module: ModuleClass<*> by lazy {
        if (ann.name.isEmpty()) {
            target.self.refType.jvmErasure.lola.asModule
        } else getModuleByName(ann.name)
    }

    override fun supplyValue(context: Context): Any {
        return try {
            moduleInstanceSuppliers.firstNotNullOf { it.get(module, context) }
        } catch (e: Throwable) {
            log.error { "An error occurred while supplying dependency for '$target': '${e.message}'." }
            e.printStackTrace()
        }
    }
}