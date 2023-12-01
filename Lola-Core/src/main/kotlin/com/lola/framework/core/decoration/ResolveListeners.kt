package com.lola.framework.core.decoration

import com.lola.framework.core.LAnnotatedElement
import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

// - ANYWHERE

// - - OTHER ELEMENTS

interface ResolveElementAnywhereListener<T : Decorated> : Decoration<T> {
    fun onElementFoundAnywhere(element: LAnnotatedElement)
}

interface ResolveClassAnywhereListener<T : Decorated> : Decoration<T>  {
    fun <T : Any> onClassFoundAnywhere(clazz: LClass<T>)
}

interface ResolveConstructorAnywhereListener<T : Decorated> : Decoration<T>  {
    fun <T : Any> onConstructorFoundAnywhere(constructor: LCallable<T, KFunction<T>>)
}

// - - ALL CALLABLES

interface ResolveCallableAnywhereListener<T : Decorated> : Decoration<T>  {
    fun <R> onCallableFoundAnywhere(member: LCallable<R, KCallable<R>>)
}

interface ResolvePropertyAnywhereListener<T : Decorated> : Decoration<T>  {
    fun <R> onPropertyFoundAnywhere(property: LCallable<R, KProperty<R>>)
}

interface ResolveFunctionAnywhereListener<T : Decorated> : Decoration<T>  {
    fun <R> onFunctionFoundAnywhere(function: LCallable<R, KFunction<R>>)
}

// - - STATIC CALLABLES

interface ResolveStaticCallableAnywhereListener<T : Decorated> : Decoration<T>  {
    fun <R> onStaticCallableFoundAnywhere(member: LCallable<R, KCallable<R>>)
}

interface ResolveStaticPropertyAnywhereListener<T : Decorated> : Decoration<T>  {
    fun <R> onStaticPropertyFoundAnywhere(property: LCallable<R, KProperty<R>>)
}

interface ResolveStaticFunctionAnywhereListener<T : Decorated> : Decoration<T>  {
    fun <R> onStaticFunctionFoundAnywhere(function: LCallable<R, KFunction<R>>)
}

// - - MEMBER CALLABLES

interface ResolveMemberCallableAnywhereListener<T : Decorated> : Decoration<T> {
    fun <R> onMemberCallableFoundAnywhere(member: LCallable<R, KCallable<R>>)
}

interface ResolveMemberPropertyAnywhereListener<T : Decorated> : Decoration<T>  {
    fun <T : Any, R> onMemberPropertyFoundAnywhere(property: LCallable<R, KProperty1<T, R>>)
}

interface ResolveMemberFunctionAnywhereListener<T : Decorated> : Decoration<T>  {
    fun <R> onMemberFunctionFoundAnywhere(function: LCallable<R, KFunction<R>>)
}

// - IN CLASS

interface ResolveConstructorListener<T : Any> : Decoration<LClass<T>> {
    fun onConstructorFound(constructor: LCallable<T, KFunction<T>>)
}

interface ResolveMemberListener<T : Any> : Decoration<LClass<T>> {
    fun onMemberFound(member: LCallable<*, *>)
}

interface ResolveMemberPropertyListener<T : Any> : Decoration<LClass<T>> {
    fun onPropertyFound(property: LCallable<*, KProperty1<T, *>>)
}

interface ResolveMemberFunctionListener<T : Any> : Decoration<LClass<T>> {
    fun onFunctionFound(function: LCallable<*, KFunction<*>>)
}