package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

class LClass<T : Any> internal constructor(val kClass: KClass<T>) : Decorated() {
    /**
     * Class level context. Extends [Lola.context].
     */
    val context: Context = Context(mutableListOf(Lola.context))

    /**
     * Creates new instance for this [KClass] using first constructor,
     * which parameters can be completed with parameters from [params] and [initialize] it.
     *
     * @param params Map of parameters invoker can provide.
     * @param propertyValues Initial values of new class properties.
     * @param ctxInitializer Callback to initialize context created for the new instance.
     * @return New instance object.
     * @throws NullPointerException If no constructor present for the given parameters.
     * @throws NullPointerException If [KClass.objectInstance] is not null (for singleton object class).
     */
    fun createInstance(
        params: Map<KParameter, Any?> = emptyMap(),
        propertyValues: Map<KProperty<*>, Any?> = emptyMap(),
        ctxInitializer: (Context) -> Unit = {}
    ): T {
        log.debug { "Creating instance of class '$this' with params: '${params.toJSON()}'." }
        for (constructor in kClass.constructors) {
            log.trace { "Trying to construct with constructor '$constructor'." }
            if (params.size == constructor.parameters.size ||
                constructor.parameters.all {
                    it.isOptional || params.containsKey(it) ||
                            it.lola.hasDecoration<ValueSupplier<*, *>>()
                }
            ) {
                val instance = constructor.lola.callBy(context, params)
                initialize(instance, propertyValues, ctxInitializer)
                return instance
            }
        }
        log.error { "An error occurred while constructing class '$this'." }
        log.error { "No constructor applicable for parameters '${params.toJSON()}':" }
        kClass.constructors.forEach { log.error { " - $it" } }
        throw NullPointerException("No constructor present for the given parameters.")
    }

    /**
     * Initialize existing [instance], map context to it and initialize properties
     * from [propertyValues] and with property initializers, call [CreateInstanceListener]s for this class.
     *
     * @param instance Instance of current class created by direct constructor invocation (Avoiding [createInstance] method).
     * @param propertyValues Values to initialize instance properties with.
     * @param ctxInitializer Callback to initialize context created for the instance.
     */
    fun initialize(
        instance: T,
        propertyValues: Map<KProperty<*>, Any?>,
        ctxInitializer: (Context) -> Unit = {}
    ) {
        val ctx = Context(mutableListOf(context))
        ctx.register { ctx }
        ctx.register<LClass<T>> { this }
        ctxInitializer(ctx)
        log.trace { "Created instance object '$instance'." }
        instanceToContext[instance] = ctx

        log.trace { "Initializing properties for class '$this'." }
        kClass.memberProperties.forEach { prop ->
            if (propertyValues.containsKey(prop)) {
                (prop as KMutableProperty<*>).setter.call(instance, propertyValues[prop])
            } else {
                prop.lola.getDecorations(ValueSupplier::class).forEach {
                    val value = it.supplyValue(ctx)
                    log.trace { "Initializing property '$prop' with value '$value'." }
                    (prop as KMutableProperty<*>).setter.call(instance, value)
                }
            }
        }
        getDecorations<CreateInstanceListener<T>>().forEach { it.onCreateInstance(instance, context) }
    }

    fun <T : Decoration<*>> hasDecoratedMembers(decoration: KClass<out T>): Boolean {
        return kClass.members.any { it.lola.hasDecoration(decoration) }
    }

    fun <T : Decoration<*>> getDecoratedMembers(decoration: KClass<out T>): Sequence<T> {
        return kClass.members.asSequence().mapNotNull { it.lola.getDecorations(decoration).firstOrNull() }
    }

    inline fun <reified T : Decoration<*>> hasDecoratedMembers(): Boolean = hasDecoratedMembers(T::class)

    inline fun <reified T : Decoration<*>> getDecoratedMembers(): Sequence<T> = getDecoratedMembers(T::class)

    @Suppress("UNCHECKED_CAST")
    override fun <D : Decorated> decorate(decoration: Decoration<D>) {
        super.decorate(decoration)
        // Not 'when' because decoration can implement multiple interfaces
        if (decoration is ResolveMemberListener<*>) kClass.members.forEach { decoration.onMemberFound(it.lola) }
        if (decoration is ResolveMemberFunctionListener<*>) kClass.memberFunctions.forEach {
            decoration.onFunctionFound(
                it.lola
            )
        }
        if (decoration is ResolveMemberPropertyListener<*>) kClass.memberProperties.forEach {
            (decoration as ResolveMemberPropertyListener<T>).onPropertyFound(it.lola)
        }
        if (decoration is ResolveConstructorListener<*>) {
            kClass.constructors.forEach { (decoration as ResolveConstructorListener<T>).onConstructorFound(it.lola) }
        }
    }

    override fun toString(): String {
        return kClass.toString()
    }
}
