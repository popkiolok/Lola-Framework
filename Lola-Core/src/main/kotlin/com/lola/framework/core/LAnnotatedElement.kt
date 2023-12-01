package com.lola.framework.core

import com.lola.framework.core.decoration.Decorated
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import kotlin.reflect.KType

abstract class LAnnotatedElement : Decorated() {
    abstract val self: KAnnotatedElement

    override fun toString(): String {
        return self.toString()
    }
}

/**
 * Returns [KParameter.type] if [KAnnotatedElement] is [KParameter], [KCallable.returnType] if it is [KCallable],
 * throws [IllegalStateException] otherwise.
 */
val KAnnotatedElement.refType: KType
    get() = when (this) {
        is KParameter -> type
        is KCallable<*> -> returnType
        else -> throw IllegalStateException("'$this' is not KParameter or KCallable.")
    }