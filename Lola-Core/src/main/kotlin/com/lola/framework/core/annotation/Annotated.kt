package com.lola.framework.core.annotation

interface Annotated {
    val annotations: AnnotationResolver
}

/**
 * Checks if annotation of type [T] is present.
 *
 * @param T The type of the annotation.
 */
inline fun <reified T : Annotation> Annotated.hasAnnotation(): Boolean = annotations.hasAnnotation(T::class)

/**
 * Retrieves the annotation of type [T].
 *
 * @param T The type of the annotation.
 * @return Annotation instance.
 * @throws NullPointerException If such annotation not present.
 */
inline fun <reified T : Annotation> Annotated.getAnnotation(): T = annotations.getAnnotation(T::class)

/**
 * Retrieves the annotation of type [T].
 *
 * @param T The type of the annotation.
 * @return Annotation instance, or null, if such annotation not present.
 */
inline fun <reified T : Annotation> Annotated.findAnnotation(): T? = annotations.findAnnotation(T::class)