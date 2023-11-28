package com.lola.framework.core.kotlin

import com.lola.framework.core.LType
import com.lola.framework.core.LClass
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

class KotlinType(override val kType: KType, override val clazz: KClass<*> = kType.jvmErasure) : LType {
    override val name = this.clazz.qualifiedName ?: this.clazz.jvmName

    override val nullable: Boolean
        get() = kType.isMarkedNullable

    override val clazz: LClass?
        get() = cachedContainer ?: getKotlinContainer(this.clazz)?.also { cachedContainer = it }
    private var cachedContainer: LClass? = null

    override fun equals(other: Any?): Boolean {
        if (other !is KotlinType) {
            return false
        }
        return other.nullable == nullable && other.clazz == this.clazz
    }

    override fun hashCode(): Int {
        return if (nullable) this.clazz.hashCode() else this.clazz.hashCode().inv()
    }
}