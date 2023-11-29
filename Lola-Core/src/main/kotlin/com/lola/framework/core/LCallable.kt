package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.util.Option
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import com.lola.framework.core.decoration.ValueSupplier
import kotlin.reflect.KClass

class LCallable<R, T : KCallable<R>>(val kCallable: T) : Decorated() {
    /**
     * Calls this function in [context] with the specified mapping of parameters to arguments and returns the result.
     * If a parameter is not found in the mapping, is not optional (as per [KParameter.isOptional])
     * and cannot be supplied by any [ValueSupplier], exception is thrown.
     */
    fun callBy(context: Context, args: Map<KParameter, Any?> = emptyMap()): R {
        val newMap = HashMap<KParameter, Any?>()
        kCallable.parameters.forEach { param ->
            newMap[param] = if (args.containsKey(param)) {
                args[param]
            } else {
                param.lola.getDecorations(ValueSupplier::class).let {
                    if (it.isEmpty()) return@forEach else it.first().supplyValue(context)
                }
            }
        }
        return kCallable.callBy(newMap)
    }

    fun <T : Decoration<*>> hasDecoratedParameters(decoration: KClass<T>): Boolean {
        return kCallable.parameters.any { it.lola.hasDecoration(decoration) }
    }

    fun <T : Decoration<*>> getDecoratedParameters(decoration: KClass<T>): Sequence<T> {
        return kCallable.parameters.asSequence().mapNotNull { it.lola.getDecorations(decoration).firstOrNull() }
    }

    inline fun <reified T : Decoration<*>> hasDecoratedParameters(): Boolean = hasDecoratedParameters(T::class)

    inline fun <reified T : Decoration<*>> getDecoratedParameters(): Sequence<T> = getDecoratedParameters(T::class)
}
