package com.lola.framework.core.impl

import com.lola.framework.core.*
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.ValueSupplier
import com.lola.framework.core.log
import com.lola.framework.core.util.Option
import kotlin.reflect.KParameter

abstract class AbstractCallable<R> : LCallable<R> {
    override fun callBy(context: Context, args: Map<LParameter, Any?>): R {
        val newMap = HashMap<LParameter, Any?>()
        parameters.forEach { param ->
            newMap[param] = if (args.containsKey(param)) {
                args[param]
            } else {
                param.getDecorations(ValueSupplier::class).let {
                    if (it.isEmpty()) return@forEach else it.first().supplyValue(context)
                }
            }
        }
        return callBySkipSuppliers(newMap)
    }

    @Suppress("UNCHECKED_CAST")
    @Deprecated(
        "This method does not get required parameters from ValueSuppliers.",
        ReplaceWith("callBy(args[parameters.first()]!!.context, args as Map<LParameter, Any?>)")
    )
    override fun callBy(args: Map<KParameter, Any?>): R {
        log.warn { "function callBy(args: Map<KParameter, Any?>) called on callable '$this' is not supported and may not work correctly." }
        return callBy(
            if (parameters.isEmpty() || parameters[0].kind == KParameter.Kind.VALUE) {
                globalContext
            } else args[parameters[0]]!!.context, args as Map<LParameter, Any?>
        )
    }

    protected abstract fun callBySkipSuppliers(args: Map<LParameter, Any?>): R
}