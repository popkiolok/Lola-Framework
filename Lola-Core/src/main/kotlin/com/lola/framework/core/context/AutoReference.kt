package com.lola.framework.core.context

import com.lola.framework.core.LAnnotatedElement
import com.lola.framework.core.LCallable
import com.lola.framework.core.LParameter
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.decoration.ForAnnotated
import com.lola.framework.core.decoration.ValueSupplier
import com.lola.framework.core.refType
import kotlin.reflect.jvm.jvmErasure

@ForAnnotated(Auto::class)
class AutoReference(override val target: LAnnotatedElement, ann: Auto) : ValueSupplier<LAnnotatedElement, Any?> {
    private val key: Any

    init {
        key = ann.stringKey.ifEmpty { target.self.refType.jvmErasure }
    }

    override fun supplyValue(context: Context): Any? {
        return context[key]
    }
}