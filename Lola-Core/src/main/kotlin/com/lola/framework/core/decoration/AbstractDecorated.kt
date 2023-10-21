package com.lola.framework.core.decoration

import com.lola.framework.core.log
import com.lola.framework.core.toJSON
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf

/**
 * [Decorated] container or element.
 */
abstract class AbstractDecorated<D : Decoration<*>> : Decorated<D> {
    override val decorations: Map<KClass<out D>, Collection<D>> = HashMap()

    @Suppress("UNCHECKED_CAST")
    override fun decorate(decoration: D) {
        log.debug { "Applying decoration $decoration for $this." }
        (decorations as HashMap).computeIfAbsent(decoration::class) { ArrayList() } as MutableCollection += decoration
        // For some reason, allSuperclasses property doesn't include primary interfaces for Mockito generated classes during tests.
        for (superclass in (decoration::class.allSuperclasses + decoration::class.java.interfaces.map {
            it.kotlin
        }).toSet()) {
            if (superclass.isSubclassOf(Decoration::class)) {
                (decorations as HashMap).computeIfAbsent(superclass as KClass<out D>) { ArrayList() } as MutableCollection += decoration
            }
        }
    }

    override fun <T : D> hasDecoration(clazz: KClass<out T>): Boolean {
        return decorations.containsKey(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : D> getDecorations(clazz: KClass<out T>): Collection<T> {
        return decorations.getOrElse(clazz) { emptyList() } as Collection<T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : D> getDecoration(clazz: KClass<out T>): T? {
        return decorations[clazz]?.firstOrNull() as T?
    }

    override fun toString() = toJSON()
}