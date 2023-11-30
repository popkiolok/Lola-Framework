package com.lola.framework.core

import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.jvmErasure

internal val callables: MutableMap<KCallable<*>, LCallable<*, *>> = WeakHashMap()

@Suppress("UNCHECKED_CAST")
val <T : KCallable<R>, R> T.lola: LCallable<R, T>
    get() = callables.computeIfAbsent(this) {
        LCallable(this, (instanceParameter ?: extensionReceiverParameter)?.type?.jvmErasure?.lola ?: Lola)
    } as LCallable<R, T>