package com.lola.framework.core.decoration

import com.lola.framework.core.context.Context

/**
 * Supplies value to initialize some reference.
 *
 * @param D The type of the decorated object.
 * @param T The type of value to supply.
 */
interface ValueSupplier<D : Decorated, T : Any?> : Decoration<D> {
    /**
     * @param context The context initialization happens in.
     * @return Supplied value.
     */
    fun supplyValue(context: Context): T
}