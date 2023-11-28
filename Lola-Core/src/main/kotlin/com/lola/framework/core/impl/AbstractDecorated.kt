package com.lola.framework.core.impl

import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.log
import com.lola.framework.core.toJSON
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf

abstract class AbstractDecorated : Decorated {
    override val decorations: Map<KClass<out Decoration<*>>, Collection<Decoration<*>>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    override fun decorate(decoration: Decoration<*>) {
        log.debug { "Applying decoration '$decoration' to '$this'." }
        (decorations as HashMap).computeIfAbsent(decoration::class) { ArrayList() } as MutableCollection += decoration
        // For some reason, allSuperclasses property doesn't include primary interfaces for Mockito generated classes during tests.
        for (superclass in (decoration::class.allSuperclasses + decoration.javaClass.interfaces.map { it.kotlin }).toSet()) {
            if (superclass.isSubclassOf(Decoration::class)) {
                (decorations as HashMap).computeIfAbsent(superclass as KClass<out Decoration<*>>) { ArrayList() } as MutableCollection += decoration
            }
        }
    }

    override fun hasDecoration(clazz: KClass<out Decoration<*>>): Boolean {
        return decorations.containsKey(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Decoration<*>> getDecorations(clazz: KClass<T>): Collection<T> {
        return decorations.getOrElse(clazz) { emptyList() } as Collection<T>
    }

    override fun <T : Decoration<*>> getDecoration(clazz: KClass<T>): T {
        return clazz.cast(decorations[clazz]?.first())
    }
}