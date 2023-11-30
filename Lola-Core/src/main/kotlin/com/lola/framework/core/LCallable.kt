package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.util.Option
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import com.lola.framework.core.decoration.ValueSupplier
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter

class LCallable<R, T : KCallable<R>>(
    val kCallable: T,
    /**
     * Holder of the [LCallable]. [LClass] for callables in class (even static) and [Lola] for non-class callables.
     */
    private val holder: Decorated
) : Decorated() {
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

    @Suppress("UNCHECKED_CAST")
    override fun <T : Decorated> decorate(decoration: Decoration<T>) {
        super.decorate(decoration)
        if (holder is LClass<*>) {
            if (holder.kClass.members.contains(kCallable)) {
                holder.onDecoratedMember(
                    this as LCallable<*, KCallable<*>>,
                    decoration as Decoration<LCallable<*, KCallable<*>>>
                )
            } else if (kCallable is KFunction<*> && holder.kClass.constructors.contains(kCallable)) {
                onDecoratedConstructor(holder, decoration)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Decorated, P : Any> onDecoratedConstructor(holder: LClass<P>, decoration: Decoration<T>) {
        holder.onDecoratedConstructor(
            this as LCallable<P, KFunction<P>>,
            decoration as Decoration<LCallable<P, KFunction<P>>>
        )
    }

    override fun toString(): String {
        return kCallable.toString()
    }
}
