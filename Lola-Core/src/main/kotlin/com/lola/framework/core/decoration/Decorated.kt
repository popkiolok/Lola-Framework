package com.lola.framework.core.decoration

import kotlin.reflect.KClass

/**
 * Allows extending container or component behavior by adding decorations to it.
 *
 * @param D Allowed decoration types.
 */
interface Decorated<D : Decoration<*>> {
    /**
     * Decorations, associated by their classes and all superclasses, which are subclasses of [Decoration].
     */
    val decorations: Map<KClass<out D>, Collection<D>>

    /**
     * Applies a decoration.
     * After adding, decoration will be accessible by it own class and all it superclasses,
     * which are subclasses of [Decoration].
     *
     * @param decoration the decoration to be added.
     */
    fun decorate(decoration: D)

    /**
     * @param clazz Class or superclass of the decoration object.
     * @return True if there is at least one decoration
     * which class or one of superclasses is [clazz], false otherwise.
     */
    fun <T : D> hasDecoration(clazz: KClass<out T>): Boolean

    /**
     * @param clazz Class or superclass to find decorations by.
     * @return Collection of decorations which class or one of superclasses is [clazz].
     */
    fun <T : D> getDecorations(clazz: KClass<out T>): Collection<T>

    /**
     * @param clazz Class or superclass to find decoration by.
     * @return Decoration which class or one of superclasses is [clazz].
     */
    fun <T : D> getDecoration(clazz: KClass<out T>): T?
}

/**
 * [Decorated.hasDecoration] that gets decoration type from reified type token.
 */
inline fun <reified T : Decoration<*>> Decorated<in T>.hasDecoration() = hasDecoration(T::class)

/**
 * [Decorated.getDecorations] that gets decoration type from reified type token.
 */
inline fun <reified T : Decoration<*>> Decorated<in T>.getDecorations() = getDecorations(T::class)

/**
 * [Decorated.getDecoration] that gets decoration type from reified type token.
 */
inline fun <reified T : Decoration<*>> Decorated<in T>.getDecoration() = getDecoration(T::class)