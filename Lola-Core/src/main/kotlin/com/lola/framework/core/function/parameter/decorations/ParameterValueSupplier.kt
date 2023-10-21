package com.lola.framework.core.function.parameter.decorations

import com.lola.framework.core.decoration.ValueSupplier
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.function.parameter.ParameterDecoration

/**
 * Represents an interface for parameter initializers.
 */
interface ParameterValueSupplier : ParameterDecoration, ValueSupplier<Parameter, Any?>