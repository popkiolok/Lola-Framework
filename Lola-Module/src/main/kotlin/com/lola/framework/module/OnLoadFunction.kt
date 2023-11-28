package com.lola.framework.module

import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.LFunction
import java.lang.IllegalStateException

class OnLoadFunction(override val self: LFunction<Unit>) : Decoration<LFunction<Unit>> {
    init {
        if (self.parameters.size > 1) {
            throw IllegalStateException("Function annotated as OnLoad must not have parameters except instance. But function $self has ${self.parameters.size} parameters.")
        }
    }
}