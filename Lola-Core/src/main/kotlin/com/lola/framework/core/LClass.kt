package com.lola.framework.core

import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.CreateInstanceListener
import kotlin.reflect.KClass

interface LClass<T : Any> : KClass<T>, Decorated {

    /**
     * Non-static properties and functions (excluding constructors) declared in this class and all it superclasses,
     * externally added members. Superclasses members are before declared members in this collection.
     */
    override val members: Collection<LCallable<*>>

    /**
     * Non-static properties declared in this class and all it superclasses, externally added properties.
     * Superclasses properties are before declared properties in this collection.
     */
    val memberProperties: Collection<LProperty<*>>

    /**
     * Non-static functions declared in this class and all it superclasses, externally added functions.
     * Superclasses functions are before declared functions in this collection.
     */
    val memberFunctions: Collection<LFunction<*>>

    override val constructors: Collection<LFunction<T>>

    /**
     * All currently found subclasses of this class, including non-direct ones.
     */
    val subclasses: Collection<LClass<out T>>

    /**
     * All superclasses of this class, including non-direct ones.
     */
    val superclasses: Collection<LClass<out T>>

    override val nestedClasses: Collection<LClass<*>>

    override val supertypes: List<LType>

    override val sealedSubclasses: List<LClass<out T>>

    /**
     * Class level context. Extends [globalContext].
     */
    val context: Context

    fun <T : Decoration<*>> hasDecoratedMembers(clazz: KClass<out T>): Boolean

    fun <T : Decoration<*>> getDecoratedMembers(clazz: KClass<out T>): Sequence<T>

    fun isSubclassOf(base: LClass<*>): Boolean

    fun isSubclassOf(base: KClass<*>): Boolean

    /**
     * Creates new instance for this [LClass] using first constructor,
     * which parameters can be completed with parameters from [params] and [initialize] it.
     *
     * @param params Map of parameters invoker can provide.
     * @param propertyValues Initial values of new class properties.
     * If there are [LProperty.constructorParameters] for used constructor,
     * they will be added to [params] and associated with values from this map.
     * @param ctxInitializer Callback to initialize context created for the new instance.
     * @return New instance object.
     * @throws NullPointerException If no constructor present for the given parameters.
     * @throws NullPointerException If [objectInstance] is not null (for singleton object class).
     */
    fun createInstance(
        params: Map<LParameter, Any?> = emptyMap(),
        propertyValues: Map<LProperty<*>, Any?> = emptyMap(),
        ctxInitializer: (Context) -> Unit = {}
    ): T

    /**
     * Initialize existing [instance], map context to it and initialize properties
     * from [propertyValues] and with property initializers, call [CreateInstanceListener]s for this class.
     *
     * @param instance Instance of current class created by direct constructor invocation (Avoiding [createInstance] method).
     * @param propertyValues Values to initialize instance properties with.
     * @param ctxInitializer Callback to initialize context created for the instance.
     */
    fun initialize(instance: T, propertyValues: Map<LProperty<*>, Any?>, ctxInitializer: (Context) -> Unit = {})

    /**
     * Adds a member to the class, invoking add member listeners present for this class.
     *
     * @param member Member to be added.
     */
    fun addMember(member: LCallable<*>)

    fun hasDefaultConstructor(): Boolean
}

inline fun <reified T : Decoration<*>> LClass<*>.hasDecoratedMembers(): Boolean = hasDecoratedMembers(T::class)

inline fun <reified T : Decoration<*>> LClass<*>.getDecoratedMembers(): Sequence<T> = getDecoratedMembers(T::class)