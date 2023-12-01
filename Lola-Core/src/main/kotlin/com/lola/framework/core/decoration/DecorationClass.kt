package com.lola.framework.core.decoration

import com.lola.framework.core.*
import com.lola.framework.core.context.Auto
import com.lola.framework.core.context.AutoReference
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure


abstract class DecorationClass<T : Decoration<*>>(final override val target: LClass<T>) : Decoration<LClass<T>> {
    init {
        target.self.constructorsParameters.forEach {
            if (it.type.jvmErasure.isSubclassOf(Decorated::class)) {
                it.lola.let { lp -> lp.decorateIfAbsent { AutoReference(lp, Auto("DecorationTarget")) } }
            }
        }
    }
}