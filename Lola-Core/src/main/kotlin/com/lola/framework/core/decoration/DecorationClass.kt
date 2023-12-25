package com.lola.framework.core.decoration

import com.lola.framework.core.*
import com.lola.framework.core.context.Auto
import com.lola.framework.core.context.AutoReference
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure


abstract class DecorationClass<T : Decoration<*>>(final override val target: LClass<T>) : Decoration<LClass<T>> {
    protected val targetParam: LParameter = target.self.constructorsParameters.first {
        it.type.jvmErasure.isSubclassOf(Decorated::class)
    }.lola

    fun isApplicableTo(decorated: Decorated): Boolean {
        return decorated::class.isSubclassOf(targetParam.self.type.jvmErasure)
    }
}