package com.lola.framework.core.kotlin

import com.lola.framework.core.Type
import com.lola.framework.core.container.Container
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

class KotlinType(override val kType: KType, override val clazz: KClass<*> = kType.jvmErasure) : Type {
    override val name = clazz.qualifiedName ?: clazz.jvmName

    override val nullable: Boolean
        get() = kType.isMarkedNullable

    override val container: Container?
        get() = cachedContainer ?: getKotlinContainer(clazz)?.also { cachedContainer = it }
    private var cachedContainer: Container? = null

    override fun equals(other: Any?): Boolean {
        if (other !is KotlinType) {
            return false
        }
        return other.nullable == nullable && other.clazz == clazz
    }

    override fun hashCode(): Int {
        return if (nullable) clazz.hashCode() else clazz.hashCode().inv()
    }
}