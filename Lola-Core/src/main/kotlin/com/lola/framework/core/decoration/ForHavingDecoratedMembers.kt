package com.lola.framework.core.decoration

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForHavingDecoratedMembers(val decoration: KClass<out Decoration<*>>)