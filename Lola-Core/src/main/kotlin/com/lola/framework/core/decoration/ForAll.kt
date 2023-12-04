package com.lola.framework.core.decoration

import com.lola.framework.core.LAnnotatedElement
import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

/**
 * Annotate decoration should be applied to every possible [Decorated] matching type of [Decoration.target].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForAll

@ForAnnotated(ForAll::class)
class ForAllDecorator<T : Decoration<*>>(target: LClass<T>) : DecorationClass<T>(target) {
    init {
        Lola.decorate(object : ResolveElementListener<Lola> {
            override val target: Lola
                get() = Lola

            override fun onElementFound(element: LAnnotatedElement) {
                if (element::class.isSubclassOf(targetParam.self.type.jvmErasure)) {
                    element.decorate(target.createInstance { it["DecorationTarget"] = element })
                }
            }
        })
    }
}