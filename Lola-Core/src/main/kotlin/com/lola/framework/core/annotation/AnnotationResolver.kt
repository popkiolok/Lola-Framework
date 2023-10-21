package com.lola.framework.core.annotation

import kotlin.reflect.KClass

/**
 * Something that can have annotations.
 */
interface AnnotationResolver {
    /**
     * Checks if the specified annotation is present.
     *
     * @param annotation the annotation to check.
     */
    fun hasAnnotation(annotation: KClass<out Annotation>): Boolean

    /**
     * Retrieves the annotation of the specified type.
     *
     * @param annotation the annotation to retrieve.
     * @return Annotation instance.
     * @throws NullPointerException If the specified [annotation] not present.
     */
    fun <T : Annotation> getAnnotation(annotation: KClass<out T>): T

    /**
     * Retrieves the annotation of the specified type.
     *
     * @param annotation the annotation to retrieve.
     * @return Annotation instance, or null, if [annotation] not present.
     */
    fun <T : Annotation> findAnnotation(annotation: KClass<out T>): T?
}