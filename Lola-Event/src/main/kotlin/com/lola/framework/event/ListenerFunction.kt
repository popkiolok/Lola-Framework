package com.lola.framework.event

import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.ForAnnotated
import com.lola.framework.core.LFunction

/**
 * Function that listens for event calls.
 */
@ForAnnotated(Listener::class)
class ListenerFunction(override val self: LFunction<Unit>, val info: Listener) : Decoration<LFunction<Unit>>