package com.lola.framework.module

/**
 * Annotate function that should be called on module unloading (for example, to free resources).
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnUnload