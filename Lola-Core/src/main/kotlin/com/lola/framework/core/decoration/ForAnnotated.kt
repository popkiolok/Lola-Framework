package com.lola.framework.core.decoration

import com.lola.framework.core.*
import com.lola.framework.core.context.Auto
import com.lola.framework.core.context.AutoReference
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
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
    private val annotationParameter: KParameter?

    init {
        annotationParameter =
            target.self.constructorsParameters.firstOrNull { it.type.jvmErasure == annotation }
        Lola.decorate(object : ResolveElementListener<Lola> {
            override val target: Lola
                get() = Lola

            override fun onElementFound(element: LAnnotatedElement) {
                val elemAnnotation = element.self.annotations.firstOrNull { it.annotationClass == annotation }
                if (elemAnnotation != null && isApplicableTo(element)) {
                    val params = buildMap {
                        put(targetParam.self, element)
                        if (annotationParameter != null) put(annotationParameter, elemAnnotation)
                    }
                    element.decorate(target.createInstance(params))
                }
            }
        })
    }
}