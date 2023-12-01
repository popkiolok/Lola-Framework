package com.lola.framework.core.decoration

import com.lola.framework.core.*
import com.lola.framework.core.context.Auto
import com.lola.framework.core.context.AutoReference
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure

class ForAnnotatedDecorator<T : Decoration<*>>(target: LClass<T>, ann: ForAnnotated) : DecorationClass<T>(target),
    ResolveElementAnywhereListener<LClass<T>> {

    private val annotation: KClass<out Annotation> = ann.annotation

    init {
        target.self.constructorsParameters.forEach {
            if (it.type.jvmErasure == annotation) {
                it.lola.let { lp -> lp.decorate(AutoReference(lp, Auto("DecorationAnnotation"))) }
            }
        }
    }

    override fun onElementFoundAnywhere(element: LAnnotatedElement) {
        element.self.annotations.firstOrNull { it.annotationClass == annotation }?.let { ann ->
            element.decorate(target.createInstance {
                it["DecorationTarget"] = element
                it["DecorationAnnotation"] = ann
            })
        }
    }
}