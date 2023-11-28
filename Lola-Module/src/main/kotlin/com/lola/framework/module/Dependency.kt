package com.lola.framework.module

import com.lola.framework.core.LType
import com.lola.framework.core.container.context.Context
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.decoration.ValueSupplier
import com.lola.framework.core.LParameter
import com.lola.framework.core.LProperty

abstract class Dependency<T : Decorated<*>>(final override val self: T, valueType: LType, ann: Dep) :
    ValueSupplier<T, Any?> {
    private val module: ModuleContainer<*> by lazy {
        if (ann.name.isEmpty()) {
            ModuleRegistry[valueType.clazz]
        } else ModuleRegistry[ann.name]
    }

    override fun supplyValue(context: Context): Result<Any?> {
        return runCatching { (context[ModuleInstanceStorage::class] as ModuleInstanceStorage).load(module).instance }
            .onFailure {
                log.error { "An error occurred while supplying dependency for '$self': '${it.message}'." }
                it.printStackTrace()
            }
    }
}

class DependencyProperty<T>(self: LProperty<T>, dep: Dep) : Dependency<LProperty<T>>(self, self.returnType, dep)

class DependencyParameter(self: LParameter, dep: Dep) : Dependency<LParameter>(self, self.type, dep)