package com.lola.framework.core

import java.util.*
import kotlin.reflect.KCallable

internal val callables: MutableMap<KCallable<*>, LCallable<*, *>> = WeakHashMap()

@Suppress("UNCHECKED_CAST")
val <T : KCallable<R>, R> T.lola: LCallable<R, T>
    get() = callables.computeIfAbsent(this) { LCallable(this) } as LCallable<R, T>