package com.lola.framework.core.kotlin

import com.lola.framework.core.impl.AbstractParameter
import kotlin.reflect.KParameter

class KotlinParameter(val kParameter: KParameter) :
    AbstractParameter(kParameter.name ?: "namelessParameter", KotlinType(kParameter.type)) {
    override val annotations = KotlinAnnotationResolver(kParameter)

    override val isOptional = kParameter.isOptional
}