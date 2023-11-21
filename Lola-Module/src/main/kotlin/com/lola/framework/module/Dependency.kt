package com.lola.framework.module

import com.lola.framework.core.Type
import com.lola.framework.core.annotation.Annotated
import com.lola.framework.core.container.context.Context
import com.lola.framework.core.decoration.ValueSupplier
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.function.parameter.decorations.ParameterValueSupplier
import com.lola.framework.core.kotlin.getKotlinContainer
import com.lola.framework.core.property.Property
import com.lola.framework.core.property.decorations.PropertyValueSupplier

abstract class Dependency<T : Annotated>(final override val self: T, valueType: Type) :
    ValueSupplier<T, Any?> {
    private val module: ModuleContainer by lazy {
        val ann = self.annotations.getAnnotation(Dep::class)
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

class DependencyProperty(self: Property) : Dependency<Property>(self, self.type),
    PropertyValueSupplier

class DependencyParameter(self: Parameter) : Dependency<Parameter>(self, self.type),
    ParameterValueSupplier