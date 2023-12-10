package com.lola.framework.event

import com.lola.framework.core.LCallable
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.ForAnnotated
import kotlin.reflect.KFunction

/**
 * Annotate function as event listener.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Listener(val event: String, val priority: Priority = Priority.DEFAULT)

@ForAnnotated(Listener::class)
class ListenerFunction(override val target: LCallable<Unit, KFunction<Unit>>, val data: Listener) :
    Decoration<LCallable<Unit, KFunction<Unit>>>
