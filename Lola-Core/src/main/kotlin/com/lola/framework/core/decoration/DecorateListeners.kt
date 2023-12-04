package com.lola.framework.core.decoration

import com.lola.framework.core.LAnnotatedElement
import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import com.lola.framework.core.LParameter
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction

interface DecorateElementListener<T : Decorated> : DecorateClassListener<T>, DecorateConstructorListener<T>,
    DecorateMemberListener<T>, DecorateParameterListener<T> {
    fun onDecoratedElement(decoration: Decoration<LAnnotatedElement>)

    override fun <T : Any> onDecoratedClass(decoration: Decoration<LClass<T>>) {
        onDecoratedElement(decoration as Decoration<LAnnotatedElement>)
    }

    override fun <T : Any> onDecoratedConstructor(decoration: Decoration<LCallable<T, KFunction<T>>>) {
        onDecoratedElement(decoration as Decoration<LAnnotatedElement>)
    }

    override fun <T> onDecoratedMember(decoration: Decoration<LCallable<T, KCallable<T>>>) {
        onDecoratedElement(decoration as Decoration<LAnnotatedElement>)
    }

    override fun onDecoratedParameter(decoration: Decoration<LParameter>) {
        onDecoratedElement(decoration as Decoration<LAnnotatedElement>)
    }
}

interface DecorateClassListener<T : Decorated> : Decoration<T> {
    fun <T : Any> onDecoratedClass(decoration: Decoration<LClass<T>>)
}

interface DecorateConstructorListener<T : Decorated> : Decoration<T> {
    fun <T : Any> onDecoratedConstructor(decoration: Decoration<LCallable<T, KFunction<T>>>)
}

interface DecorateMemberListener<T : Decorated> : Decoration<T> {
    fun <T> onDecoratedMember(decoration: Decoration<LCallable<T, KCallable<T>>>)
}

interface DecorateParameterListener<T : Decorated> : Decoration<T> {
    fun onDecoratedParameter(decoration: Decoration<LParameter>)
}

interface DecorateListener<T : Decorated> : Decoration<T> {
    fun onDecorated(decoration: Decoration<T>)
}