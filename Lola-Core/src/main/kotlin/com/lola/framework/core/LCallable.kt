package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.util.Option
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import com.lola.framework.core.decoration.ValueSupplier

interface LCallable<R> : KCallable<R>, Decorated {
    override val parameters: List<LParameter>

    override val returnType: LType

    /**
     * Calls this function with the specified mapping of parameters to arguments and returns the result.
     * If a parameter is not found in the mapping, is not optional (as per [KParameter.isOptional])
     * and cannot be supplied by any [ValueSupplier], exception is thrown.
     */
    fun callBy(context: Context, args: Map<LParameter, Any?> = emptyMap()): R

    @Deprecated("This method does not get required parameters from ValueSuppliers.")
    override fun callBy(args: Map<KParameter, Any?>): R

    fun undecoratedCopy(): LCallable<R>
}