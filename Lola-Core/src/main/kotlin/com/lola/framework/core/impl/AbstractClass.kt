package com.lola.framework.core.impl

import com.lola.framework.core.*
import com.lola.framework.core.context.Context
import com.lola.framework.core.LFunction
import com.lola.framework.core.LParameter
import com.lola.framework.core.decoration.*
import com.lola.framework.core.log
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.KVisibility

abstract class AbstractClass<T : Any> : LClass<T>, AbstractDecorated() {
    override val members: MutableList<LCallable<*>> = ArrayList()

    // Must copy collections (see 'decorate' method in this class), final for this reason.
    final override val memberProperties: Collection<LProperty<*>>
        get() = members.filterIsInstance<LProperty<*>>()
    final override val memberFunctions: Collection<LFunction<*>>
        get() = members.filterIsInstance<LFunction<*>>()

    override val constructors: MutableCollection<LFunction<T>> =
        PriorityQueue(Comparator.comparingInt { -it.parameters.size })

    override val subclasses: MutableList<LClass<out T>> = ArrayList()

    override val context = Context(mutableListOf(globalContext))

    // For superclass members, that should be added before it.
    private var firstDeclaredMemberIndex = 0

    protected fun afterInit() {
        superclasses.forEach { clazz -> clazz.decorate(MemberInheritor(clazz)) }
        addListeners.values.forEach { it.onClassFound(this) }
        lolaClasses += this
        log.debug { "Created class $this." }
    }

    override fun createInstance(
        params: Map<LParameter, Any?>,
        propertyValues: Map<LProperty<*>, Any?>,
        ctxInitializer: (Context) -> Unit
    ): T {
        log.debug { "Creating instance of class '$this' with params: '${params.toJSON()}'." }
        for (constructor in constructors) {
            log.trace { "Trying to construct with constructor '$constructor'." }
            val cParams = HashMap<LParameter, Any?>()
            constructor.parameters.forEach { if (params.containsKey(it)) cParams[it] = params[it] }
            propertyValues.forEach { (prop, propVal) ->
                prop.constructorParameters[constructor]?.let { cParams[it] = propVal }
            }
            if (cParams.size == constructor.parameters.size ||
                constructor.parameters.all { it.isOptional || cParams.containsKey(it) }
            ) {
                val instance = constructor.callBy(context, cParams)
                initialize(
                    instance,
                    propertyValues.filterKeys { !cParams.containsKey(it.constructorParameters[constructor]) },
                    ctxInitializer
                )
                return instance
            }
        }
        log.error { "An error occurred while constructing class '$this'." }
        log.error { "No constructor applicable for parameters '${params.toJSON()}':" }
        constructors.forEach { log.error { " - $it" } }
        throw NullPointerException("No constructor present for the given parameters.")
    }

    override fun initialize(instance: T, propertyValues: Map<LProperty<*>, Any?>, ctxInitializer: (Context) -> Unit) {
        val ctx = Context(mutableListOf(context))
        ctx.register { ctx }
        ctx.register<LClass<T>> { this }
        ctxInitializer(ctx)
        log.trace { "Created instance object '$instance'." }
        instanceToContext[instance] = ctx

        log.trace { "Initializing properties for class '$this'." }
        memberProperties.forEach { prop ->
            if (propertyValues.containsKey(prop)) {
                (prop as LMutableProperty<*>).setter.call(instance, propertyValues[prop])
            } else {
                prop.getDecorations(ValueSupplier::class).forEach {
                    val value = it.supplyValue(ctx)
                    log.trace { "Initializing property '$prop' with value '$value'." }
                    (prop as LMutableProperty<*>).setter.call(instance, value)
                }
            }
        }
        getDecorations<CreateInstanceListener<T>>().forEach { it.onCreateInstance(instance, context) }
    }

    override fun addMember(member: LCallable<*>) {
        addMemberBy(member) { members += it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun decorate(decoration: Decoration<*>) {
        super.decorate(decoration)
        // Not 'when' because decoration can implement multiple interfaces
        // Copy collection because code in decorations can add members and ConcurrentModificationException will be thrown.
        // memberFunctions and memberProperties already copy collections
        if (decoration is AddMemberListener<*>) members.toTypedArray().forEach { decoration.onMemberAdded(it) }
        if (decoration is AddFunctionListener<*>) memberFunctions.forEach { decoration.onFunctionAdded(it) }
        if (decoration is AddPropertyListener<*>) memberProperties.forEach { decoration.onPropertyAdded(it) }
        if (decoration is AddConstructorListener<*>) {
            constructors.forEach { (decoration as AddConstructorListener<T>).onConstructorAdded(it) }
        }
    }

    override fun hasDefaultConstructor(): Boolean {
        return constructors.any { it.parameters.isEmpty() }
    }

    private fun isNotPresent(member: LCallable<*>): Boolean {
        return if (member is LProperty<*>) {
            memberProperties.none { it.name == member.name }
        } else {
            member as LFunction<*>
            memberFunctions.none {
                it.name == member.name && it.parameters.size == member.parameters.size &&
                        it.parameters.withIndex().all { (i, p) -> p.type == member.parameters[i].type }
            }
        }
    }

    override fun toString() = toJSON()

    private inline fun addMemberBy(member: LCallable<*>, adder: (LCallable<*>) -> Unit) {
        log.trace { "Adding member '$member' to the class '$this'." }
        assert(!members.contains(member))
        adder(member)
        getDecorations<AddMemberListener<T>>().forEach { it.onMemberAdded(member) }
        when (member) {
            is LProperty -> getDecorations<AddPropertyListener<T>>().forEach { it.onPropertyAdded(member) }
            is LFunction -> getDecorations<AddFunctionListener<T>>().forEach { it.onFunctionAdded(member) }
            else -> throw IllegalArgumentException()
        }
    }

    inner class MemberInheritor<T : Any>(override val self: LClass<T>) : AddMemberListener<T> {
        override fun onMemberAdded(member: LCallable<*>) {
            if (/*member.visibility != KVisibility.PRIVATE && */isNotPresent(member)) {
                addMemberBy(member.undecoratedCopy()) { members.add(firstDeclaredMemberIndex, it) }
                firstDeclaredMemberIndex++
            }
        }
    }
}