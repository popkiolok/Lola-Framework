package com.lola.framework.core.function

import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.decoration.AbstractDecorated
import com.lola.framework.core.log
import com.lola.framework.core.toJSON
import com.lola.framework.core.util.Option
import com.lola.framework.core.util.SparseArray
import com.lola.framework.core.util.complete

abstract class AbstractFunction : Function, AbstractDecorated<FunctionDecoration>() {
    override fun tryInvoke(instance: ContainerInstance, params: SparseArray<Any?>): Option<Any?> {
        params.complete { index ->
            for (initializer in parameters[index].initializers) {
                val result = initializer.supplyValue(instance.context)
                result.onSuccess {
                    return@complete it
                }
            }
            return Option.empty()
        }
        log.trace { "Invoking function $this with parameters ${params.toJSON()}." }
        return Option(invoke(instance, params))
    }
}