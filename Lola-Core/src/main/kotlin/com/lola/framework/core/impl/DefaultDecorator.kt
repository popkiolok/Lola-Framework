package com.lola.framework.core.impl

import com.lola.framework.core.LClass
import com.lola.framework.core.decoration.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * Process annotations [ForAnnotated], [ForSubclasses] etc.
 */
class DefaultDecorator : FoundClassListener {
    val decorations: MutableList<LClass<out Decoration<*>>> = ArrayList()

    @Suppress("UNCHECKED_CAST")
    override fun onClassFound(clazz: LClass<*>) {
        if (clazz.isSubclassOf(Decoration::class)) {
            decorations += clazz as LClass<out Decoration<*>>
        }
        decorations.forEach { dClass ->
            val annotated = dClass.findAnnotation<ForAnnotated>()
                ?.let { ann -> clazz.annotations.any { it::class == ann.annotation } } ?: false
            val subclass = dClass.findAnnotation<ForSubclasses>()
                ?.let { ann -> clazz.isSubclassOf(ann.parent) } ?: false
            if (annotated || subclass) {
                clazz.decorate(dClass.createInstance())
            }
            val forHavingDecoratedMembers = dClass.findAnnotation<ForHavingDecoratedMembers>()
        }
    }
}