package com.lola.framework.module

import com.lola.framework.core.LCallable
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.ForAnnotated
import java.lang.IllegalStateException
import kotlin.reflect.KFunction

/**
 * Annotate function that should be called on module unloading (for example, to free resources).
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnUnload

@ForAnnotated(OnUnload::class)
class OnUnloadFunction(override val target: LCallable<Unit, KFunction<Unit>>) :
    Decoration<LCallable<Unit, KFunction<Unit>>> {
    init {
        val nParams = target.self.parameters.size
        if (nParams > 1) {
            log.error { "Function annotated as OnUnload must not have parameters except instance. But function '$target' has '$nParams' parameters." }
            throw IllegalStateException("Too much parameters for function '$target'.")
        }
    }
}