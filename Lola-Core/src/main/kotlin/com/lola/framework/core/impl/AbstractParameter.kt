package com.lola.framework.core.impl

import com.lola.framework.core.LParameter
import kotlin.reflect.KParameter

abstract class AbstractParameter(
    inSourceName: String?,
    final override val kind: KParameter.Kind,
    final override val index: Int
) : LParameter, AbstractDecorated() {
    override val name: String = inSourceName ?: if (kind != KParameter.Kind.VALUE) "INSTANCE" else "PARAM$index"
}