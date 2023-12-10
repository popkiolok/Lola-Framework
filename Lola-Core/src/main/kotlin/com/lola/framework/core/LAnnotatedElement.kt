package com.lola.framework.core

import com.lola.framework.core.decoration.Decorated
import kotlin.reflect.*

/**
 * Lola API extension for Kotlin's reflection [KAnnotatedElement], that makes it [Decorated], allowing associating
 * decorations with [KAnnotatedElement]s.
 */
abstract class LAnnotatedElement : Decorated() {
    /**
     * Kotlin's reflection [KAnnotatedElement] this object is associated with.
     */
    abstract val self: KAnnotatedElement

    /**
     * Returns string representation of [self].
     */
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