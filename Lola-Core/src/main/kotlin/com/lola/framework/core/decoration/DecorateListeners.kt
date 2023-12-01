package com.lola.framework.core.decoration

import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction

interface DecorateClassListener : Decoration<Lola> {
    fun <T : Any> onDecoratedClass(clazz: LClass<T>, decoration: Decoration<LClass<T>>)
}

interface DecorateClassConstructorListener : Decoration<Lola> {
    fun <T : Any> onDecoratedClassConstructor(
        clazz: LClass<T>,
        constructor: LCallable<T, KFunction<T>>,
        decoration: Decoration<LCallable<T, KFunction<T>>>
    )
}

interface DecorateClassMemberListener<T : Decorated> : Decoration<T> {
    fun <T : Any> onDecoratedClassMember(
        clazz: LClass<T>,
        member: LCallable<*, KCallable<*>>,
        decoration: Decoration<LCallable<*, KCallable<*>>>
    )
}

interface DecorateConstructorListener<T : Any> : Decoration<LClass<T>> {
    fun onDecoratedConstructor(
        constructor: LCallable<T, KFunction<T>>,
        decoration: Decoration<LCallable<T, KFunction<T>>>
    )
}

interface DecorateMemberListener<T : Any> : Decoration<LClass<T>> {
    fun onDecoratedMember(member: LCallable<*, KCallable<*>>, decoration: Decoration<LCallable<*, KCallable<*>>>)
}

interface DecorateListener<T : Decorated> : Decoration<T> {
    fun onDecorated(decoration: Decoration<T>)
}