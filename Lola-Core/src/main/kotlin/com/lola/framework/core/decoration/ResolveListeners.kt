package com.lola.framework.core.decoration

import com.lola.framework.core.*
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

interface ResolveDecoratedListener<T : Decorated> : ResolveElementListener<T>, ResolveLolaListener<T> {
    fun onDecoratedFound(decorated: Decorated)

    override fun onLolaFound(lola: Lola) {
        onDecoratedFound(lola)
    }

    override fun onElementFound(element: LAnnotatedElement) {
        onDecoratedFound(element)
    }
}

interface ResolveElementListener<T : Decorated> : ResolveClassListener<T>, ResolveConstructorListener<T>,
    ResolveCallableListener<T>, ResolveParameterListener<T> {
    fun onElementFound(element: LAnnotatedElement)

    override fun <T : Any> onClassFound(clazz: LClass<T>) {
        onElementFound(clazz)
    }

    override fun <T : Any> onConstructorFound(constructor: LCallable<T, KFunction<T>>) {
        onElementFound(constructor)
    }

    override fun <R> onCallableFound(callable: LCallable<R, KCallable<R>>) {
        onElementFound(callable)
    }

    override fun onParameterFound(parameter: LParameter) {
        onElementFound(parameter)
    }
}

interface ResolveLolaListener<T : Decorated> : Decoration<T> {
    fun onLolaFound(lola: Lola)
}

interface ResolveClassListener<T : Decorated> : Decoration<T> {
    fun <T : Any> onClassFound(clazz: LClass<T>)
}

interface ResolveParameterListener<T : Decorated> : Decoration<T> {
    fun onParameterFound(parameter: LParameter)
}

// ALL CALLABLES

interface ResolveConstructorListener<T : Decorated> : Decoration<T> {
    fun <T : Any> onConstructorFound(constructor: LCallable<T, KFunction<T>>)
}

interface ResolveCallableListener<T : Decorated> : ResolveStaticCallableListener<T>, ResolveMemberCallableListener<T> {
    fun <R> onCallableFound(callable: LCallable<R, KCallable<R>>)

    override fun <R> onStaticCallableFound(callable: LCallable<R, KCallable<R>>) {
        onCallableFound(callable)
    }

    override fun <R> onMemberCallableFound(callable: LCallable<R, KCallable<R>>) {
        onCallableFound(callable)
    }
}

interface ResolvePropertyListener<T : Decorated> : ResolveStaticPropertyListener<T>, ResolveMemberPropertyListener<T> {
    fun <R> onPropertyFound(property: LCallable<R, KProperty<R>>)

    override fun <R> onStaticPropertyFound(property: LCallable<R, KProperty<R>>) {
        onPropertyFound(property)
    }

    override fun <T : Any, R> onMemberPropertyFound(property: LCallable<R, KProperty1<T, R>>) {
        onPropertyFound(property as LCallable<R, KProperty<R>>)
    }
}

interface ResolveFunctionListener<T : Decorated> : ResolveStaticFunctionListener<T>, ResolveMemberFunctionListener<T> {
    fun <R> onFunctionFound(function: LCallable<R, KFunction<R>>)

    override fun <R> onStaticFunctionFound(function: LCallable<R, KFunction<R>>) {
        onFunctionFound(function)
    }

    override fun <R> onMemberFunctionFound(function: LCallable<R, KFunction<R>>) {
        onFunctionFound(function)
    }
}

// STATIC CALLABLES

interface ResolveStaticCallableListener<T : Decorated> : Decoration<T> {
    fun <R> onStaticCallableFound(callable: LCallable<R, KCallable<R>>)
}

interface ResolveStaticPropertyListener<T : Decorated> : ResolveStaticCallableListener<T> {
    fun <R> onStaticPropertyFound(property: LCallable<R, KProperty<R>>)

    override fun <R> onStaticCallableFound(callable: LCallable<R, KCallable<R>>) {
        if (callable.self is KProperty<R>) {
            onStaticPropertyFound(callable as LCallable<R, KProperty<R>>)
        }
    }
}

interface ResolveStaticFunctionListener<T : Decorated> : ResolveStaticCallableListener<T> {
    fun <R> onStaticFunctionFound(function: LCallable<R, KFunction<R>>)

    override fun <R> onStaticCallableFound(callable: LCallable<R, KCallable<R>>) {
        if (callable.self is KFunction<R>) {
            onStaticFunctionFound(callable as LCallable<R, KFunction<R>>)
        }
    }
}

// MEMBER CALLABLES

interface ResolveMemberCallableListener<T : Decorated> : Decoration<T> {
    fun <R> onMemberCallableFound(callable: LCallable<R, KCallable<R>>)
}

interface ResolveMemberPropertyListener<T : Decorated> : ResolveMemberCallableListener<T> {
    fun <T : Any, R> onMemberPropertyFound(property: LCallable<R, KProperty1<T, R>>)

    override fun <R> onMemberCallableFound(callable: LCallable<R, KCallable<R>>) {
        if (callable.self is KProperty) {
            onMemberPropertyFound(callable as LCallable<R, KProperty1<T, R>>)
        }
    }
}

interface ResolveMemberFunctionListener<T : Decorated> : ResolveMemberCallableListener<T> {
    fun <R> onMemberFunctionFound(function: LCallable<R, KFunction<R>>)

    override fun <R> onMemberCallableFound(callable: LCallable<R, KCallable<R>>) {
        if (callable.self is KFunction) {
            onMemberFunctionFound(callable as LCallable<R, KFunction<R>>)
        }
    }
}
