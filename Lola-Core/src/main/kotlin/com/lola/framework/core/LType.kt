package com.lola.framework.core

import kotlin.reflect.KType

interface LType : KType {
    /**
     * Returns [LClass] associated with this type.
     * If a [LClass] instance for this type has already been created, it will be returned.
     * Otherwise, new [LClass] instance will be created.
     */
    val clazz: LClass<*>

    /**
     * @param other Another object.
     * @return True if [other] object is [LType] object represents the same type.
     */
    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun toString(): String
}