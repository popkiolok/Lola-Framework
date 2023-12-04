package com.lola.framework.core

import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.jvmErasure

internal val callables: MutableMap<KCallable<*>, LCallable<*, *>> = WeakHashMap()

/**
 * Gets or creates new [LCallable] for this [KCallable].
 */
val <T : KCallable<R>, R> T.lola: LCallable<R, T>
    get() = @Suppress("UNCHECKED_CAST") (callables.computeIfAbsent(this) {
        LCallable(this, runCatching { (instanceParameter ?: extensionReceiverParameter)?.type?.jvmErasure?.lola }.getOrNull() ?: Lola)
    } as LCallable<R, T>)