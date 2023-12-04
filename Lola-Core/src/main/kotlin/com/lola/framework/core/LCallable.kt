package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.*
import kotlin.reflect.*

/**
 * Lola API extension for Kotlin's reflection [KCallable].
 *
 * @param R Callable return type.
 * @param T Type of Kotlin's reflection [KCallable] this object is associated with.
 * @property holder Holder of the [LCallable]. [LClass] for callables in class and extension callables (including
 * constructors) and [Lola] for non-class callables.
 * @constructor Create empty L callable
 */
class LCallable<R, T : KCallable<R>>(override val self: T, val holder: Decorated) : LAnnotatedElement(),
    DecorateListener<LCallable<R, T>>, DecorateParameterListener<LCallable<R, T>> {

    /**
     * Returns if this callable is class constructor (this also means [holder] is [LClass] and [self] is [KFunction]).
     */
    val isConstructor: Boolean
        get() = holder is LClass<*> && self is KFunction<*> && holder.self.constructors.contains(self)

    /**
     * Returns if this callable is class member (this also means [holder] is [LClass]).
     */
    val isMember: Boolean
        get() = holder is LClass<*> && holder.self.members.contains(self)

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
    override fun <D : Decorated> decorate(decoration: Decoration<D>) {
        super.decorate(decoration)
        if (holder is DecorateMemberListener<*> && isMember) {
            holder.onDecoratedMember(decoration as Decoration<LCallable<R, KCallable<R>>>)
        }
        if (holder is DecorateConstructorListener<*> && isConstructor) {
            (holder as DecorateConstructorListener<*>)
                .onDecoratedConstructor(decoration as Decoration<LCallable<Any, KFunction<Any>>>)
        }
        onDecorated(decoration as Decoration<LCallable<R, T>>)
    }

    override val target: LCallable<R, T>
        get() = this

    override fun onDecorated(decoration: Decoration<LCallable<R, T>>) {
        if (decoration is ResolveParameterListener) {
            self.parameters.forEach { decoration.onParameterFound(it.lola) }
        }
    }

    override fun onDecoratedParameter(decoration: Decoration<LParameter>) {
        getDecorations<DecorateParameterListener<*>>().forEach { it.onDecoratedParameter(decoration) }
        if (holder is DecorateParameterListener<*>) {
            holder.onDecoratedParameter(decoration)
        }
    }
}