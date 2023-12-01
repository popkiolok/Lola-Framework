package com.lola.framework.core.decoration

import com.lola.framework.core.LClass
import kotlin.reflect.KClass

/**
 * Annotate decoration should be applied to every possible [Decorated] matching type of [Decoration.target].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForAll

/**
 * Annotate decoration should be applied to every [Decorated] that is annotated with [annotation].
 *
 * @property annotation Target annotation class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForAnnotated(val annotation: KClass<out Annotation>)

/**
 * Annotate decoration should be applied to every [LClass] that is subclass of [parent].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForSubclasses(val parent: KClass<*>)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForHavingDecoratedMembers(val decoration: KClass<out Decoration<*>>)