package com.lola.framework.core.function.parameter

import com.lola.framework.core.Type
import com.lola.framework.core.decoration.AbstractDecorated
import com.lola.framework.core.decoration.getDecorations
import com.lola.framework.core.function.parameter.decorations.ParameterValueSupplier

abstract class AbstractParameter(override val name: String, override val type: Type) :
    Parameter, AbstractDecorated<ParameterDecoration>() {
    override val initializers: Collection<ParameterValueSupplier>
        get() {
            return getDecorations<ParameterValueSupplier>()
        }
}