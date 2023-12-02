package com.lola.framework.core.decoration

import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction

interface DecorateClassListener<T : Decorated> : Decoration<T> {
    fun <T : Any> onDecoratedClass(decoration: Decoration<LClass<T>>)
}

interface DecorateClassConstructorListener<T : Decorated> : Decoration<T> {
    fun <T : Any> onDecoratedClassConstructor(decoration: Decoration<LCallable<T, KFunction<T>>>)
}

interface DecorateClassMemberListener<T : Decorated> : Decoration<T> {
    fun <T> onDecoratedClassMember(decoration: Decoration<LCallable<T, KCallable<T>>>)
}

interface DecorateConstructorListener<T : Any> : Decoration<LClass<T>> {
    fun onDecoratedConstructor(decoration: Decoration<LCallable<T, KFunction<T>>>)
}

interface DecorateMemberListener<T : Any> : Decoration<LClass<T>> {
    fun <T> onDecoratedMember(decoration: Decoration<LCallable<T, KCallable<T>>>)
}

interface DecorateListener<T : Decorated> : Decoration<T> {
    fun onDecorated(decoration: Decoration<T>)
}