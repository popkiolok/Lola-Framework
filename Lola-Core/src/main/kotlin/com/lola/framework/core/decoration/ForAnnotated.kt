package com.lola.framework.core.decoration

import kotlin.reflect.KClass

/**
 * Annotate decoration should be applied to every [Decorated] that is annotated with [annotation].
 *
 * @property annotation Target annotation class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForAnnotated(val annotation: KClass<out Annotation>)