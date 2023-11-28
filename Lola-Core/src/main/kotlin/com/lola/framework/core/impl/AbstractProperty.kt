package com.lola.framework.core.impl

import com.lola.framework.core.LProperty
import com.lola.framework.core.util.Option

abstract class AbstractProperty<V> : LProperty<V>, AbstractCallable<V>() {
    override val hasDefaultValue: Boolean
        get() = !isLateinit && (constructorParameters.isEmpty() || constructorParameters.values.any { it.isOptional })

    override val defaultValue: Option<Any?>
        get() = Option.empty() // TODO
}