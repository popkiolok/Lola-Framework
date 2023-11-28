package com.lola.framework.core.kotlin

import java.util.*
import kotlin.reflect.KClass

internal val kotlinContainersByKClass: MutableMap<KClass<*>, LClassKotlin> = WeakHashMap()

/**
 * Gets [LClassKotlin] associated with [KClass], if the [kClass] is container and is registered.
 *
 * @return [LClassKotlin] instance, associated with this class, or null if there is no
 * registered container for class.
 */
fun <T : Any> getKotlinContainer(kClass: KClass<*>): LClassKotlin<T>? {
    return kotlinContainersByKClass[kClass]
}

val <T : Any> KClass<T>.lola: LClassKotlin<T>
    get() = getKotlinContainer(this)!!

val <T : Any> T.lolaClass: LClassKotlin<out T>
    get() = this::class.lola