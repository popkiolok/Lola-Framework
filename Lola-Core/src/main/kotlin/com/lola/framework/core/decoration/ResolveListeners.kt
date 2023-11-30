package com.lola.framework.core.decoration

import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

/**
 * Makes implementation listening for finding new [LClass]es (Creating [LClass] instances).
 * When [ResolveClassListener] subscribed, [onClassFound] will be called for every already found class.
 */
interface ResolveClassListener : Decoration<Lola> {
    fun onClassFound(clazz: LClass<*>)
}

/**
 * Makes decoration listening for resolving class constructors.
 * When [ResolveClassConstructorListener] decoration applied to [Lola],
 * [onClassConstructorFound] will be called for every existing constructor in every found class.
 */
interface ResolveClassConstructorListener : Decoration<Lola> {
    fun <T : Any> onClassConstructorFound(clazz: LClass<T>, constructor: LCallable<T, KFunction<T>>)
}


/**
 * Makes decoration listening for resolving class members (excluding constructors).
 * When [ResolveClassMemberListener] decoration applied to [Lola],
 * [onClassMemberFound] will be called for every existing member in every found class.
 */
interface ResolveClassMemberListener : Decoration<Lola> {
    fun <T : Any> onClassMemberFound(clazz: LClass<T>, member: LCallable<*, *>)
}

/**
 * Makes decoration listening for resolving class properties.
 * When [ResolveClassMemberPropertyListener] decoration applied to [Lola],
 * [onClassPropertyFound] will be called for every existing property in every found class.
 */
interface ResolveClassMemberPropertyListener : Decoration<Lola> {
    fun <T : Any> onClassPropertyFound(clazz: LClass<T>, property: LCallable<*, KProperty1<T, *>>)
}
/**
 * Makes decoration listening for resolving class functions (excluding constructors and property accessors).
 * When [ResolveClassMemberFunctionListener] decoration applied to [Lola],
 * [onClassFunctionFound] will be called for every existing function in every found class.
 */
interface ResolveClassMemberFunctionListener : Decoration<Lola> {
    fun <T : Any> onClassFunctionFound(clazz: LClass<T>, function: LCallable<*, KFunction<*>>)
}


/**
 * Makes decoration listening for resolving class constructors.
 * When [ResolveConstructorListener] decoration applied to class,
 * [onConstructorFound] will be called for every existing constructor in it.
 */
interface ResolveConstructorListener<T : Any> : Decoration<LClass<T>> {
    fun onConstructorFound(constructor: LCallable<T, KFunction<T>>)
}


/**
 * Makes decoration listening for resolving class members (excluding constructors).
 * When [ResolveMemberListener] decoration applied to class,
 * [onMemberFound] will be called for every existing member in it.
 */
interface ResolveMemberListener<T : Any> : Decoration<LClass<T>> {
    fun onMemberFound(member: LCallable<*, *>)
}

/**
 * Makes decoration listening for resolving class properties.
 * When [ResolveMemberPropertyListener] decoration applied to class,
 * [onPropertyFound] will be called for every existing property in it.
 */
interface ResolveMemberPropertyListener<T : Any> : Decoration<LClass<T>> {
    fun onPropertyFound(property: LCallable<*, KProperty1<T, *>>)
}
/**
 * Makes decoration listening for resolving class functions (excluding constructors and property accessors).
 * When [ResolveMemberFunctionListener] decoration applied to class,
 * [onFunctionFound] will be called for every existing function in it.
 */
interface ResolveMemberFunctionListener<T : Any> : Decoration<LClass<T>> {
    fun onFunctionFound(function: LCallable<*, KFunction<*>>)
}