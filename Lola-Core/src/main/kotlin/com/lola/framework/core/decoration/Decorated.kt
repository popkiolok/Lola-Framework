package com.lola.framework.core.decoration

import kotlin.reflect.KClass

/**
 * Allows extending container or component behavior by adding decorations to it.
 */
interface Decorated {
    /**
     * Decorations, associated by their classes and all superclasses, which are subclasses of [Decoration].
     */
    val decorations: Map<KClass<out Decoration<*>>, Collection<Decoration<*>>>

    /**
     * Applies a decoration.
     * After adding, decoration will be accessible by it own class and all it superclasses,
     * which are subclasses of [Decoration].
     *
     * @param decoration the decoration to be added.
     */
    fun decorate(decoration: Decoration<*>)

    /**
     * @param clazz Class or superclass of the decoration object.
     * @return True if there is at least one decoration which class or one of superclasses is [clazz], false otherwise.
     */
    fun hasDecoration(clazz: KClass<out Decoration<*>>): Boolean

    /**
     * @param clazz Class or superclass to find decorations by.
     * @return Collection of decorations which class or one of superclasses is [clazz].
     */
    fun <T : Decoration<*>> getDecorations(clazz: KClass<T>): Collection<T>

    /**
     * @param clazz Class or superclass to find decoration by.
     * @return Decoration which class or one of superclasses is [clazz].
     */
    fun <T : Decoration<*>> getDecoration(clazz: KClass<T>): T
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