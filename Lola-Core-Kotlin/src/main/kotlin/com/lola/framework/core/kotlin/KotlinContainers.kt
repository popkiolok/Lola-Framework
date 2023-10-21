package com.lola.framework.core.kotlin

import java.util.*
import kotlin.reflect.KClass

internal val kotlinContainersByKClass: MutableMap<KClass<*>, KotlinContainer> = WeakHashMap()

/**
 * Gets [KotlinContainer] associated with [KClass], if the [kClass] is container and is registered.
 *
 * @return [KotlinContainer] instance, associated with this class, or null if there is no
 * registered container for class.
 */
fun getKotlinContainer(kClass: KClass<*>): KotlinContainer? {
    return kotlinContainersByKClass[kClass]
}