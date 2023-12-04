package com.lola.framework.core.decoration

import com.lola.framework.core.log
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.isSubclassOf

/**
 * Allows extending the behavior of something by adding decorations to it. Decorations of a decorated object are
 * stored in [decorations].
 */
abstract class Decorated {
    /**
     * Decorations, associated by their classes and all superclasses, which are subclasses of [Decoration] (including
     * [Decoration] type itself).
     */
    val decorations: Map<KClass<out Decoration<*>>, Collection<Decoration<*>>> = HashMap()

    /**
     * Applies a decoration, invoking all [DecorateListener]s for this object after adding it to [decorations].
     * After applying, decoration will be accessible by it own class and all it superclasses,
     * which are subclasses of [Decoration] (including [Decoration] type itself).
     * If [decoration] is [DecorateListener], [DecorateListener.onDecorated] will be invoked on it for every present
     * decoration on this object.
     *
     * @param decoration the decoration to be added.
     */
    open fun <T : Decorated> decorate(decoration: Decoration<T>) {
        log.debug { "Applying decoration '$decoration' to '$this'." }
        if (decoration is DecorateListener) {
            getDecorations<Decoration<T>>().forEach { decoration.onDecorated(it) }
        }
        (decorations as HashMap).computeIfAbsent(decoration::class) { ArrayList() } as MutableCollection += decoration
        // For some reason, allSuperclasses property doesn't include primary interfaces for Mockito generated classes during tests.
        for (superclass in (decoration::class.allSuperclasses + decoration.javaClass.interfaces.map { it.kotlin }).toSet()) {
            if (superclass.isSubclassOf(Decoration::class)) {
                @Suppress("UNCHECKED_CAST")
                decorations.computeIfAbsent(superclass as KClass<out Decoration<*>>) { ArrayList() } as MutableCollection += decoration
            }
        }
        getDecorations<DecorateListener<T>>().forEach { it.onDecorated(decoration) }
    }

    /**
     * Applied a decoration if there are no decorations of type [T] on this object.
     *
     * @param T Type of decoration that will be used to check if there are decorations of the same type.
     * @param decoration Function returns decoration that should be applied.
     */
    inline fun <reified T : Decoration<*>> decorateIfAbsent(decoration: () -> T) {
        if (!hasDecoration<T>()) {
            decorate(decoration())
        }
    }

    /**
     * If [clazz] is subclass of [Decoration] (or is [Decoration] itself), returns if there are decorations which class
     * or one of superclasses is [clazz], otherwise always returns false.
     *
     * @param clazz Class or superclass to find decorations by.
     */
    fun hasDecoration(clazz: KClass<out Decoration<*>>): Boolean {
        return decorations.containsKey(clazz)
    }

    /**
     * If [clazz] is subclass of [Decoration] (or is [Decoration] itself), returns all decorations on this object which
     * class or one of superclasses is [clazz], otherwise returns an empty collection.
     *
     * @param clazz Class or superclass to find decorations by.
     */
    fun <T : Decoration<*>> getDecorations(clazz: KClass<T>): Collection<T> {
        @Suppress("UNCHECKED_CAST")
        return decorations.getOrElse(clazz) { emptyList() } as Collection<T>
    }

    /**
     * If [clazz] is subclass of [Decoration] (or is [Decoration] itself), returns first decoration on this object which
     * class or one of superclasses is [clazz], otherwise throws exception.
     *
     * @param clazz Class or superclass to find decoration by.
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