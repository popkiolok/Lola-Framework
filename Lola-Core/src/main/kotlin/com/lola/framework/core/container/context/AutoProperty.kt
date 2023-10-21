package com.lola.framework.core.container.context

import com.lola.framework.core.property.Property
import com.lola.framework.core.property.decorations.PropertyValueSupplier

class AutoProperty(override val self: Property) : PropertyValueSupplier {
    private val key: Any

    init {
        val ann = self.annotations.getAnnotation(Auto::class)
        key = ann.stringKey.ifEmpty { self.type.clazz }
    }

    override fun supplyValue(context: Context): Result<Any?> {
        return runCatching { context[key] }
    }
}