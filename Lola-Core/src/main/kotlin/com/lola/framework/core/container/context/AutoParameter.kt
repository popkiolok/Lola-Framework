package com.lola.framework.core.container.context

import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.function.parameter.decorations.ParameterValueSupplier

class AutoParameter(override val self: Parameter) : ParameterValueSupplier {
    private val key: Any

    init {
        val ann = self.annotations.getAnnotation(Auto::class)
        key = ann.stringKey.ifEmpty { self.type.clazz }
    }

    override fun supplyValue(context: Context): Result<Any?> {
        return runCatching { context[key] }
    }
}