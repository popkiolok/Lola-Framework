package com.lola.framework.core.decoration

import com.lola.framework.core.container.context.Context

/**
 * Supplies value during some reference initialization.
 *
 * @param D The type of the decorated object.
 * @param T The type of value to supply.
 */
interface ValueSupplier<D : Any, T : Any?> : Decoration<D> {
    /**
     * @param context The context initialization happens in.
     * @return Supplied value wrapped in [Result], or [Result.Failure] if value can not be supplied.
     */
    fun supplyValue(context: Context): Result<T>
}