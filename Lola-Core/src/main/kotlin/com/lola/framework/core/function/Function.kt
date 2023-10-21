package com.lola.framework.core.function

import com.lola.framework.core.Element
import com.lola.framework.core.Nameable
import com.lola.framework.core.Type
import com.lola.framework.core.annotation.Annotated
import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.function.parameter.decorations.ParameterValueSupplier
import com.lola.framework.core.util.Option
import com.lola.framework.core.util.SparseArray

/**
 * Element of container, that can be invoked.
 */
interface Function : Element, Nameable, Decorated<FunctionDecoration>,
    Annotated {
    /**
     * Function parameters.
     * First parameter should be an instance of container for functions in container.
     */
    val parameters: List<Parameter>

    /**
     * [Type] of function return value, if it is possible to identify its type.
     */
    val returnType: Type?

    /**
     * Try to invoke function with given [params], where some parameters can be not present
     * to provide by [ParameterValueSupplier]s.
     * Every [params] value will be associated with [Parameter] from [parameters] at the same index.
     *
     * @param instance The [ContainerInstance] of container containing function.
     * @param params Invocation parameters.
     * @return Optional function return value, empty if impossible to initialize all parameters.
     */
    fun tryInvoke(instance: ContainerInstance, params: SparseArray<Any?>): Option<Any?>

    /**
     * Invokes function with given [params].
     * Every [params] value will be associated with [Parameter] from [parameters] at the same index.
     *
     * @param instance The [ContainerInstance] of container containing function.
     * @param params Invocation parameters.
     * @return Function return value.
     */
    operator fun invoke(instance: ContainerInstance, params: Array<Any?> = emptyArray()): Any?
}