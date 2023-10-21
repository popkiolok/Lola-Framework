package com.lola.framework.core.function.parameter

import com.lola.framework.core.Nameable
import com.lola.framework.core.Type
import com.lola.framework.core.annotation.Annotated
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.function.parameter.decorations.ParameterValueSupplier

/**
 * [Function] parameter.
 */
interface Parameter : Nameable, Decorated<ParameterDecoration>, Annotated {
    /**
     * Returns parameter value type.
     */
    val type: Type

    /**
     * Returns unmodifiable collection of this parameter initializers.
     */
    val initializers: Collection<ParameterValueSupplier>

    val isOptional: Boolean
}