package com.lola.framework.core.decoration

import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import com.lola.framework.core.lola
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf

/**
 * Process annotations [ForAnnotated], [ForSubclasses] etc.
 */
class DefaultDecorator(override val target: Lola) : ResolveClassListener, ResolveClassConstructorListener,
    ResolveClassMemberListener, DecorateClassConstructorListener, DecorateClassMemberListener {
    @Suppress("UNCHECKED_CAST")
    override fun onClassFound(clazz: LClass<*>) {
        if (clazz.kClass.isSubclassOf(Decoration::class) &&
            clazz.kClass.annotations.any { it.annotationClass.java.`package` == javaClass.`package` }
        ) {
            clazz.decorate(DecorationClass(clazz as LClass<out Decoration<*>>))
        }
        Lola.getDecoratedClasses<DecorationClass<*>>().forEach { it.process(clazz) }
    }

    override fun <T : Any> onClassConstructorFound(clazz: LClass<T>, constructor: LCallable<T, KFunction<T>>) {
        Lola.getDecoratedClasses<DecorationClass<*>>().forEach {
            it.process(constructor)
            constructor.kCallable.parameters.forEach { param ->
                it.process(param.lola)
            }
        }
    }

    override fun <T : Any> onClassMemberFound(clazz: LClass<T>, member: LCallable<*, *>) {
        Lola.getDecoratedClasses<DecorationClass<*>>().forEach {
            it.process(member)
            member.kCallable.parameters.forEach { param ->
                it.process(param.lola)
            }
        }
    }

    override fun <T : Any> onDecoratedClassConstructor(
        clazz: LClass<T>,
        constructor: LCallable<T, KFunction<T>>,
        decoration: Decoration<LCallable<T, KFunction<T>>>
    ) {
        Lola.getDecoratedClasses<DecorationClass<*>>().forEach {
            it.onDecoratedMember(clazz, decoration)
        }
    }

    override fun <T : Any> onDecoratedClassMember(
        clazz: LClass<T>,
        member: LCallable<*, KCallable<*>>,
        decoration: Decoration<LCallable<*, KCallable<*>>>
    ) {
        Lola.getDecoratedClasses<DecorationClass<*>>().forEach {
            it.onDecoratedMember(clazz, decoration)
        }
    }
}