package com.lola.framework.core.decoration

import com.lola.framework.core.*
import com.lola.framework.core.context.Auto
import com.lola.framework.core.context.AutoReference
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

/**
 * Annotate decoration should be applied to every [Decorated] that is annotated with [annotation].
 *
 * @property annotation Target annotation class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForAnnotated(val annotation: KClass<out Annotation>)

class ForAnnotatedDecorator<T : Decoration<*>>(target: LClass<T>, ann: ForAnnotated) : DecorationClass<T>(target) {

    private val annotation: KClass<out Annotation> = ann.annotation

    init {
        target.self.constructorsParameters.forEach {
            if (it.type.jvmErasure == annotation) {
                it.lola.let { lp -> lp.decorate(AutoReference(lp, Auto("DecorationAnnotation"))) }
            }
        }
        Lola.decorate(object : ResolveElementListener<Lola> {
            override val target: Lola
                get() = Lola

            override fun onElementFound(element: LAnnotatedElement) {
                element.self.annotations.firstOrNull { it.annotationClass == annotation }?.let { ann ->
                    element.decorate(target.createInstance {
                        it["DecorationTarget"] = element
                        it["DecorationAnnotation"] = ann
                    })
                }
            }
        })
    }
}