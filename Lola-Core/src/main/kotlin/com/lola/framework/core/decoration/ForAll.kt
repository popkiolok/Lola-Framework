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
        Lola.decorate(object : ResolveDecoratedListener<Lola> {
            override val target: Lola
                get() = Lola

            override fun onDecoratedFound(decorated: Decorated) {
                if (isApplicableTo(decorated)) {
                    val params = buildMap { put(targetParam.self, decorated) }
                    val decoration = target.createInstance(params)
                    decorated.decorate(decoration)
                }
            }
        })
    }
}