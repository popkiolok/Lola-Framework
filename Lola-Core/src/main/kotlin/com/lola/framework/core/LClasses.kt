package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.ResolveClassListener
import com.lola.framework.core.decoration.getDecorations
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

val classes: Collection<LClass<*>>
    get() = classMap.values

internal val instanceToContext: MutableMap<Any, Context> = WeakHashMap()
internal val classMap: MutableMap<KClass<*>, LClass<*>> = WeakHashMap()

@Suppress("UNCHECKED_CAST")
val <T : Any> KClass<T>.lola: LClass<T>
    get() = classMap.computeIfAbsent(this) {
        val lClass = LClass(this)
        Lola.getDecorations<ResolveClassListener<*>>().forEach { it.onClassFound(lClass) }
        lClass
    } as LClass<T>

val Any?.hasContext: Boolean
    get() = this != null && instanceToContext.containsKey(this)

val Any.objectContext: Context
    get() = instanceToContext[this]
        ?: run {
            log.error { "Unable to find any context object associated with instance object '$this'." }
            log.error { "This happens when instance object is neither created with Lola-Framework API nor initialized with it." }
            throw NullPointerException("No context associated with object '$this' found.")
        }

fun getSubclasses(clazz: LClass<*>) = classes.asSequence().filter { it.self.isSubclassOf(clazz.self) && it != clazz }