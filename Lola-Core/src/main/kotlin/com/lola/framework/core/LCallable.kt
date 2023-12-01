package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.decoration.Decoration
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import com.lola.framework.core.decoration.ValueSupplier
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class LCallable<R, T : KCallable<R>>(
    override val self: T,
    /**
     * Holder of the [LCallable]. [LClass] for callables in class (even static) and [Lola] for non-class callables.
     */
    val holder: Decorated
) : LAnnotatedElement() {
    /**
     * Calls this function in [context] with the specified mapping of parameters to arguments and returns the result.
     * If a parameter is not found in the mapping, is not optional (as per [KParameter.isOptional])
     * and cannot be supplied by any [ValueSupplier], exception is thrown.
     */
    fun callBy(context: Context, args: Map<KParameter, Any?> = emptyMap()): R {
        val newMap = HashMap<KParameter, Any?>()
        self.parameters.forEach { param ->
            newMap[param] = if (args.containsKey(param)) {
                args[param]
            } else {
                param.lola.getDecorations(ValueSupplier::class).let {
                    if (it.isEmpty()) return@forEach else it.first().supplyValue(context)
                }
            }
        }
        return self.callBy(newMap)
    }

    fun <T : Decoration<*>> hasDecoratedParameters(decoration: KClass<T>): Boolean {
        return self.parameters.any { it.lola.hasDecoration(decoration) }
    }

    fun <T : Decoration<*>> getDecoratedParameters(decoration: KClass<T>): Sequence<T> {
        return self.parameters.asSequence().mapNotNull { it.lola.getDecorations(decoration).firstOrNull() }
    }

    inline fun <reified T : Decoration<*>> hasDecoratedParameters(): Boolean = hasDecoratedParameters(T::class)

    inline fun <reified T : Decoration<*>> getDecoratedParameters(): Sequence<T> = getDecoratedParameters(T::class)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Decorated> decorate(decoration: Decoration<T>) {
        super.decorate(decoration)
        if (holder is LClass<*>) {
            if (holder.self.members.contains(self)) {
                holder.onDecoratedMember(
                    this as LCallable<*, KCallable<*>>,
                    decoration as Decoration<LCallable<*, KCallable<*>>>
                )
            } else if (self is KFunction<*> && holder.self.constructors.contains(self)) {
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
}
