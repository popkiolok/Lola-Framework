package com.lola.framework.core

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

internal val parameters: MutableMap<KParameter, LParameter> = WeakHashMap()

val KParameter.lola: LParameter
    get() = parameters.computeIfAbsent(this) { LParameter(this) }