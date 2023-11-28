package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.FoundClassListener
import java.util.*
import kotlin.reflect.KClass

internal val instanceToContext: MutableMap<Any, Context> = WeakHashMap()
internal val lolaClasses: MutableList<LClass<*>> = ArrayList()

val Any.context: Context
    get() = instanceToContext[this]
        ?: run {
            log.error { "Unable to find any context object associated with instance object '$this'." }
            log.error { "This happens when instance object is neither created with Lola-Framework API nor initialized with it." }
            throw NullPointerException("No context associated with object '$this' found.")
        }

val foundLolaClasses: List<LClass<*>>
    get() = lolaClasses

internal val addListeners: MutableMap<KClass<*>, FoundClassListener> = Collections.synchronizedMap(HashMap())

fun subscribeAddContainerListener(listener: FoundClassListener) {
    if (addListeners.containsKey(listener::class)) {
        return
    }
    addListeners[listener::class] = listener
    log.info { "Found AddContainerListener '$listener'." }
    lolaClasses.forEach { listener.onClassFound(it) }
}

val globalContext = Context(ArrayList())