package com.lola.framework.core.kotlin

import com.lola.framework.core.function.parameter.AbstractParameter
import com.lola.framework.core.util.Option
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.jvmErasure

class KotlinParameter(val kParameter: KParameter) :
    AbstractParameter(kParameter.name ?: "namelessParameter", KotlinType(kParameter.type)) {
    override val annotations = KotlinAnnotationResolver(kParameter)

    override val isOptional = kParameter.isOptional
}