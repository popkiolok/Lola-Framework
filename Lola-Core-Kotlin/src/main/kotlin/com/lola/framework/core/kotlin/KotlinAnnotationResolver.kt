package com.lola.framework.core.kotlin

import com.lola.framework.core.annotation.AbstractAnnotationResolver
import com.lola.framework.core.toJSON
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

class KotlinAnnotationResolver(private val annotatedElement: KAnnotatedElement) : AbstractAnnotationResolver() {
    override fun hasAnnotation(annotation: KClass<out Annotation>): Boolean {
        return annotatedElement.annotations.any { annotation.isInstance(it) }
    }

    override fun <T : Annotation> findAnnotation(annotation: KClass<out T>) =
        annotatedElement.annotations.firstOrNull { annotation.isInstance(it) }?.let {
            annotation.cast(it)
        }
}