package com.lola.framework.core.kotlin

import com.lola.framework.core.constructor.Constructor
import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.util.Option
import com.lola.framework.core.util.PLACEHOLDER
import com.lola.framework.core.util.SparseArray
import com.lola.framework.core.util.complete
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class KotlinConstructor(kConstructor: KFunction<*>) : Constructor, KotlinFunction(kConstructor) {
    override fun tryInvoke(instance: ContainerInstance, params: SparseArray<Any?>): Option<Any> {
        var hasOptional = false
        params.complete { index ->
            for (initializer in parameters[index].valueSuppliers) {
                val result = initializer.supplyValue(instance.context)
                log.trace { "Initializing parameter ${parameters[index]} by $initializer with value $result." }
                result.onSuccess {
                    return@complete it
                }
            }
            if (!parameters[index].isOptional) {
                return Option.empty()
            } else {
                hasOptional = true
                return@complete PLACEHOLDER
            }
        }
        return Option(if (hasOptional) {
            val map = HashMap<KParameter, Any?>()
            params.forEachIndexed { i, e ->
                if (e != PLACEHOLDER) {
                    map[kFunction.parameters[i]] = e
                }
            }
            kFunction.callBy(map)
        } else {
            invoke(instance, params)
        }!!)
    }

    override fun invoke(instance: ContainerInstance, params: Array<Any?>): Any {
        return kFunction.call(*params)!!
    }
}