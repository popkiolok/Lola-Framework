package com.lola.framework.module

import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.LFunction
import java.lang.IllegalStateException

class OnUnloadFunction(override val target: LFunction<Unit>) : Decoration<LFunction<Unit>> {
    init {
        if (target.parameters.size > 1) {
            throw IllegalStateException("Function annotated as OnUnload must not have parameters except instance. But function $target has ${target.parameters.size} parameters.")
        }
    }
}