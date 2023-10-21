package com.lola.framework.event

/**
 * Annotate function as event listener.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Listener(val event: String, val priority: Priority = Priority.DEFAULT)
