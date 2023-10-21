package com.lola.framework.core.constructor

import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.function.Function
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.function.parameter.decorations.ParameterValueSupplier
import com.lola.framework.core.util.Option
import com.lola.framework.core.util.SparseArray

/**
 * Represents an interface for container constructors.
 */
interface Constructor : Function {
    /**
     * Try constructing container with given parameters, where some parameters can be not present
     * to provide by [ParameterValueSupplier]s.
     * Every [params] value will be associated with [Parameter] from [parameters] at the same index.
     *
     * @param instance The uncompleted [ContainerInstance] of constructing container.
     * @param params Invocation parameters index to value map.
     * @return Optional new container instance object, or empty if impossible to initialize all parameters.
     */
    override fun tryInvoke(instance: ContainerInstance, params: SparseArray<Any?>): Option<Any>

    /**
     * Construct container with given [params].
     * Every [params] value will be associated with [Parameter] from [parameters] at the same index.
     *
     * @param instance The uncompleted [ContainerInstance] of constructing container.
     * @param params Invocation parameters.
     * @return New container instance object.
     */
    override fun invoke(instance: ContainerInstance, params: Array<Any?>): Any
}