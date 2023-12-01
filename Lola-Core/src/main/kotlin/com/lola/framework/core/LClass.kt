package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.*
import kotlin.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

class LClass<T : Any> internal constructor(override val self: KClass<T>) : LAnnotatedElement(),
    DecorateConstructorListener<T>,
    DecorateMemberListener<T> {
    /**
     * Class level context. Extends [Lola.context].
     */
    val context: Context = Lola.context.child().also { it.register<LClass<T>> { this } }

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
        for (constructor in self.constructors) {
            if (params.size == constructor.parameters.size ||
                constructor.parameters.all {
                    it.isOptional || params.containsKey(it) ||
                            it.lola.hasDecoration<ValueSupplier<*, *>>()
                }
            ) {
                log.trace { "Constructing with constructor '$constructor'." }
                val ctx = context.child()
                ctxInitializer(ctx)
                val instance = constructor.lola.callBy(ctx, params)
                log.trace { "Created instance object '$instance'." }
                initialize(instance, propertyValues, ctx)
                return instance
            }
        }
        log.error { "An error occurred while constructing class '$this'." }
        log.error { "No constructor applicable for parameters '${params.toJSON()}':" }
        self.constructors.forEach { log.error { " - $it" } }
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
        val ctx = context.child()
        ctxInitializer(ctx)
        initialize(instance, propertyValues, ctx)
    }

    private fun initialize(
        instance: T,
        propertyValues: Map<KProperty<*>, Any?>,
        ctx: Context
    ) {
        instanceToContext[instance] = ctx

        log.trace { "Initializing properties for class '$this'." }
        self.memberProperties.forEach { prop ->
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
        return self.members.any { it.lola.hasDecoration(decoration) }
    }

    fun <T : Decoration<*>> getDecoratedMembers(decoration: KClass<out T>): Sequence<T> {
        return self.members.asSequence().mapNotNull { it.lola.getDecorations(decoration).firstOrNull() }
    }

    inline fun <reified T : Decoration<*>> hasDecoratedMembers(): Boolean = hasDecoratedMembers(T::class)

    inline fun <reified T : Decoration<*>> getDecoratedMembers(): Sequence<T> = getDecoratedMembers(T::class)

    @Suppress("UNCHECKED_CAST")
    override fun <D : Decorated> decorate(decoration: Decoration<D>) {
        super.decorate(decoration)
        // Not 'when' because decoration can implement multiple interfaces
        if (decoration is ResolveMemberListener<*>) self.members.forEach { decoration.onMemberFound(it.lola) }
        if (decoration is ResolveMemberFunctionListener<*>) self.memberFunctions.forEach {
            decoration.onFunctionFound(it.lola)
        }
        if (decoration is ResolveMemberPropertyListener<*>) self.memberProperties.forEach {
            (decoration as ResolveMemberPropertyListener<T>).onPropertyFound(it.lola)
        }
        if (decoration is ResolveConstructorListener<*>) {
            self.constructors.forEach { (decoration as ResolveConstructorListener<T>).onConstructorFound(it.lola) }
        }
    }

    override fun onDecoratedConstructor(
        constructor: LCallable<T, KFunction<T>>,
        decoration: Decoration<LCallable<T, KFunction<T>>>
    ) {
        getDecorations<DecorateConstructorListener<T>>().forEach { it.onDecoratedConstructor(constructor, decoration) }
        Lola.onDecoratedClassConstructor(this, constructor, decoration)
    }

    override fun onDecoratedMember(
        member: LCallable<*, KCallable<*>>,
        decoration: Decoration<LCallable<*, KCallable<*>>>
    ) {
        getDecorations<DecorateMemberListener<T>>().forEach { it.onDecoratedMember(member, decoration) }
        Lola.onDecoratedClassMember(this, member, decoration)
    }

    override val target: LClass<T>
        get() = this
}

val KClass<*>.constructorsParameters: Sequence<KParameter>
    get() = constructors.asSequence().flatMap { it.parameters }
