package com.lola.framework.event

import com.lola.framework.core.annotation.getAnnotation
import com.lola.framework.core.function.Function
import com.lola.framework.core.function.FunctionDecoration

/**
 * Function that listens for event calls.
 */
class EventListener(override val self: Function) : FunctionDecoration {
    /**
     * Name of event listeners listens for.
     */
    val event: String

    /**
     * Ordinal index of listener [Priority].
     */
    val priority: Int

    init {
        val ann = self.getAnnotation<Listener>()
        event = ann.event.intern()
        priority = ann.priority.ordinal
    }
}
