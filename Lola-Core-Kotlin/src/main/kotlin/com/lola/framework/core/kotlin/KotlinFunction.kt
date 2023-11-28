package com.lola.framework.core.kotlin

import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.impl.AbstractFunction
import com.lola.framework.core.util.Option
import com.lola.framework.core.util.PLACEHOLDER
import com.lola.framework.core.util.SparseArray
import com.lola.framework.core.util.complete
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

open class KotlinFunction(val kFunction: KFunction<*>) : AbstractFunction() {
    override val name = kFunction.name
    override val parameters = kFunction.parameters.map { KotlinParameter(it) }
    override val annotations = KotlinAnnotationResolver(kFunction)
    override val returnType = KotlinType(kFunction.returnType)

    override fun tryInvoke(instance: ContainerInstance, params: SparseArray<Any?>): Option<Any?> {
        var hasOptional = false
        params.complete { index ->
            for (initializer in parameters[index].valueSuppliers) {
                val result = initializer.supplyValue(instance.context)
                log.trace { "Initializing parameter ${parameters[index]} by $initializer with value $result." }
                result.onSuccess {
                    return@complete it
                }
            }
            if (!parameters[index + 1].isOptional) {
                return Option.empty()
            } else {
                hasOptional = true
                return@complete PLACEHOLDER
            }
        }
        return Option(if (hasOptional) {
            val map = HashMap<KParameter, Any?>()
            map[kFunction.parameters[0]] = instance.instance
            params.forEachIndexed { i, e ->
                if (e != PLACEHOLDER) {
                    map[kFunction.parameters[i + 1]] = e
                }
            }
            kFunction.callBy(map)
        } else {
            invoke(instance, params)
        })
    }

    override fun invoke(instance: ContainerInstance, params: Array<Any?>): Any? {
        return kFunction.call(instance.instance, *params)
    }
}