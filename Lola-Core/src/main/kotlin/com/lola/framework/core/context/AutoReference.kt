package com.lola.framework.core.context

import com.lola.framework.core.LParameter
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.decoration.ForAnnotated
import com.lola.framework.core.decoration.ValueSupplier

@ForAnnotated(Auto::class)
class AutoReference(override val self: LParameter, ann: Auto) : ValueSupplier<Decorated, Any?> {
    private val key: Any

    init {
        key = ann.stringKey.ifEmpty { self.type.clazz }
    }

    override fun supplyValue(context: Context): Any? {
        return context[key]
    }
}