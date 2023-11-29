package com.lola.framework.core.decoration

import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import com.lola.framework.core.lola
import kotlin.reflect.KFunction
import kotlin.reflect.full.isSubclassOf

/**
 * Process annotations [ForAnnotated], [ForSubclasses] etc.
 */
class DefaultDecorator(override val target: Lola) : ResolveClassListener {
    @Suppress("UNCHECKED_CAST")
    override fun onClassFound(clazz: LClass<*>) {
        if (clazz.kClass.isSubclassOf(Decoration::class) &&
            clazz.kClass.annotations.any { it.annotationClass.java.`package` == javaClass.`package` }
        ) {
            clazz.decorate(DecorationClass(clazz as LClass<out Decoration<*>>))
        }
        clazz.decorate(ClassMembersDecorator(clazz))
    }

    class ClassMembersDecorator<T : Any>(override val target: LClass<T>) : ResolveConstructorListener<T>,
        ResolveMemberListener<T> {
        init {
            Lola.getDecoratedClasses<DecorationClass<*>>().forEach { it.process(target) }
        }

        override fun onConstructorFound(constructor: LCallable<T, KFunction<T>>) {
            constructor.decorate(ParametersDecorator(constructor))
        }

        override fun onMemberFound(member: LCallable<*, *>) {
            member.decorate(ParametersDecorator(member))
        }
    }

    class ParametersDecorator<T>(override val target: LCallable<T, *>) : DecorateListener<LCallable<T, *>> {
        init {
            Lola.getDecoratedClasses<DecorationClass<*>>().forEach {
                it.process(target)
                target.kCallable.parameters.forEach { param ->
                    it.process(param.lola)
                }
            }
        }

        override fun onDecorated(decoration: Decoration<LCallable<T, *>>) {
            Lola.getDecoratedClasses<DecorationClass<*>>().forEach {
                it.onDecoratedMember(target, decoration)
            }
        }
    }
}