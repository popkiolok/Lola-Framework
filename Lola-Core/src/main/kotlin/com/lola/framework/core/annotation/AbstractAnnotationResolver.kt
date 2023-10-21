package com.lola.framework.core.annotation

import kotlin.reflect.KClass

abstract class AbstractAnnotationResolver : AnnotationResolver {
    override fun hasAnnotation(annotation: KClass<out Annotation>): Boolean {
        return findAnnotation(annotation) != null
    }

    override fun <T : Annotation> getAnnotation(annotation: KClass<out T>): T {
        return findAnnotation(annotation) ?: throw NullPointerException("No annotation $annotation present on $this.")
    }
}