package com.lola.framework.core

import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

internal val callables: MutableMap<KCallable<*>, LCallable<*, *>> = WeakHashMap()

/**
 * Gets or creates new [LCallable] for this [KCallable].
 */
val <T : KCallable<R>, R> T.lola: LCallable<R, T>
    get() = @Suppress("UNCHECKED_CAST") (callables.computeIfAbsent(this) {
        runCatching {
            isAccessible = true
            if (this is KProperty<*>) {
                getter.isAccessible = true
                if (this is KMutableProperty<*>) {
                    setter.isAccessible = true
                }
            }
        }
        LCallable(this, runCatching { (instanceParameter ?: extensionReceiverParameter)?.type?.jvmErasure?.lola }.getOrNull() ?: Lola)
    } as LCallable<R, T>)