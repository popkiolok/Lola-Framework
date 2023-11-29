package com.lola.framework.core.decoration

import com.lola.framework.core.log
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf

/**
 * Allows extending class or callable behavior by adding decorations to it.
 */
open class Decorated {
    /**
     * Decorations, associated by their classes and all superclasses, which are subclasses of [Decoration].
     */
    val decorations: Map<KClass<out Decoration<*>>, Collection<Decoration<*>>> = HashMap()

    /**
     * Applies a decoration.
     * After adding, decoration will be accessible by it own class and all it superclasses,
     * which are subclasses of [Decoration].
     *
     * @param decoration the decoration to be added.
     */
    @Suppress("UNCHECKED_CAST")
    open fun <T : Decorated> decorate(decoration: Decoration<T>) {
        log.debug { "Applying decoration '$decoration' to '$this'." }
        if (decoration is DecorateListener) {
            getDecorations<Decoration<T>>().forEach { decoration.onDecorated(it) }
        }
        (decorations as HashMap).computeIfAbsent(decoration::class) { ArrayList() } as MutableCollection += decoration
        // For some reason, allSuperclasses property doesn't include primary interfaces for Mockito generated classes during tests.
        for (superclass in (decoration::class.allSuperclasses + decoration.javaClass.interfaces.map { it.kotlin }).toSet()) {
            if (superclass.isSubclassOf(Decoration::class)) {
                decorations.computeIfAbsent(superclass as KClass<out Decoration<*>>) { ArrayList() } as MutableCollection += decoration
            }
        }
        getDecorations<DecorateListener<T>>().forEach { it.onDecorated(decoration) }
    }

    /**
     * @param clazz Class or superclass of the decoration object.
     * @return True if there is at least one decoration which class or one of superclasses is [clazz], false otherwise.
     */
    fun hasDecoration(clazz: KClass<out Decoration<*>>): Boolean {
        return decorations.containsKey(clazz)
    }

    /**
     * @param clazz Class or superclass to find decorations by.
     * @return Collection of decorations which class or one of superclasses is [clazz].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Decoration<*>> getDecorations(clazz: KClass<T>): Collection<T> {
        return decorations.getOrElse(clazz) { emptyList() } as Collection<T>
    }

    /**
     * @param clazz Class or superclass to find decoration by.
     * @return Decoration which class or one of superclasses is [clazz].
     */
    fun <T : Decoration<*>> getDecoration(clazz: KClass<T>): T {
        return clazz.cast(decorations[clazz]?.first())
    }
}

/**
 * [Decorated.hasDecoration] that gets decoration type from reified type token.
 */
inline fun <reified T : Decoration<*>> Decorated.hasDecoration() = hasDecoration(T::class)

/**
 * [Decorated.getDecorations] that gets decoration type from reified type token.
 */
inline fun <reified T : Decoration<*>> Decorated.getDecorations() = getDecorations(T::class)

/**
 * [Decorated.getDecoration] that gets decoration type from reified type token.
 */
inline fun <reified T : Decoration<*>> Decorated.getDecoration() = getDecoration(T::class)