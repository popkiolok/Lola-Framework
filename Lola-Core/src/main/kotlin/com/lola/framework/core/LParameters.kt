package com.lola.framework.core

import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

internal val parameters: MutableMap<KParameter, LParameter> = WeakHashMap()

val KParameter.lola: LParameter
    get() = parameters.computeIfAbsent(this) { LParameter(this, (callableGetter.get(this) as KCallable<*>).lola) }

private val callableGetter = run {
    val kParameterImpl = Class.forName("kotlin.reflect.jvm.internal.KParameterImpl")
    kParameterImpl.declaredFields.first { KCallable::class.java.isAssignableFrom(it.type) }.also {
        it.isAccessible = true
    }
}