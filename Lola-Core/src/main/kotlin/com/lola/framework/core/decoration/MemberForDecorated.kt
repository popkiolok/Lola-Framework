package com.lola.framework.core.decoration

import com.lola.framework.core.decoration.Decoration
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MemberForDecorated(val decoration: KClass<out Decoration<*>>)