package com.lola.framework.core.decoration

import kotlin.reflect.KClass
import com.lola.framework.core.LClass

/**
 * Annotate decoration should be applied to every [LClass] that is subclass of [parent].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForSubclasses(val parent: KClass<*>)
