package com.lola.framework.core

import com.lola.framework.core.container.Container
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Abstract representation of the type / class of value.
 */
interface Type : Nameable {
    /**
     * Returns true if value of this type can be null.
     * If the type is primitive type returns false.
     */
    val nullable: Boolean

    val clazz: KClass<*>
    val kType: KType

    /**
     * Returns [Container] associated with this type, or null, if this type cannot be associated with any [Container].
     * If a [Container] instance for this type has already been created, it will be returned.
     * Otherwise, new [Container] instance will be created.
     */
    val container: Container?

    /**
     * @param other Another type object.
     * @return True if [other] object is [Type] object represents the same type.
     */
    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}