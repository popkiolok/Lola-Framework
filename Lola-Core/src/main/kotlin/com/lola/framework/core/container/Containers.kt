package com.lola.framework.core.container

import com.lola.framework.core.container.context.Context
import java.util.*
import kotlin.reflect.KClass

// All containers instance to Container map
internal val containerPool: MutableMap<Any, ContainerInstance> = WeakHashMap()

/**
 * Gets [ContainerInstance] object for the container [instance] itself, controlled by this object.
 *
 * @param instance The instance of container.
 * @return [ContainerInstance] that [ContainerInstance.instance] is [instance] object.
 * @throws NullPointerException If [instance] is not instance of container,
 * or was created incorrectly and hasn't got [ContainerInstance] associated with it.
 */
fun getContainerInstance(instance: Any): ContainerInstance {
    return containerPool[instance]
        ?: throw NullPointerException("No Container Instance associated with object $instance exists.")
}

/**
 * [getContainerInstance] that returns null, if instance is null.
 */
@JvmName("getNullableContainerInstance")
fun getContainerInstance(instance: Any?): ContainerInstance? {
    return instance?.let { getContainerInstance(it) }
}

internal val registered: MutableList<Container> = ArrayList()

fun getFoundContainers(): List<Container> {
    return registered
}

internal val addListeners: MutableMap<KClass<*>, AddContainerListener> = Collections.synchronizedMap(HashMap())

fun subscribeAddContainerListener(listener: AddContainerListener) {
    if (addListeners.containsKey(listener::class)) {
        return
    }
    addListeners[listener::class] = listener
    getFoundContainers().forEach { listener.onContainerAdded(it) }
}

val globalContext = Context(ArrayList())