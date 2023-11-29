package com.lola.framework.core.decoration

import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import com.lola.framework.core.LParameter
import com.lola.framework.core.lola
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class DecorationClass<T : Decoration<*>>(override val target: LClass<T>) : Decoration<LClass<T>> {
    private val forAnnotated = target.kClass.findAnnotation<ForAnnotated>()
    private val forSubclasses = target.kClass.findAnnotation<ForSubclasses>()
    private val forHavingDecoratedMembers = target.kClass.findAnnotation<ForHavingDecoratedMembers>()

    init {
        target.kClass.constructors.asSequence()
            .flatMap { it.parameters }
            .forEach {
                if (it.type.jvmErasure.isSubclassOf(Decorated::class)) {
                    it.lola.let { lp -> lp.decorate(TargetParameter(lp)) }
                } else if (forAnnotated != null && it.type.jvmErasure == forAnnotated.annotation) {
                    it.lola.let { lp -> lp.decorate(AnnotationParameter(lp)) }
                }
            }
    }

    fun process(clazz: LClass<*>) {
        val ann = getAnnotated(clazz.kClass)
        if (ann != null || isSubclass(clazz.kClass)) {
            clazz.decorate(makeInstance(clazz, ann))
        }
    }

    fun process(callable: LCallable<*, *>) {
        val ann = getAnnotated(callable.kCallable)
        if (ann != null) {
            callable.decorate(makeInstance(callable, ann))
        }
    }

    fun process(param: LParameter) {
        val ann = getAnnotated(param.kParameter)
        if (ann != null) {
            param.decorate(makeInstance(param, ann))
        }
    }

    fun onDecoratedMember(member: LCallable<*, *>, decoration: Decoration<*>) {
        forHavingDecoratedMembers?.let { ann ->
            if (decoration::class == ann.decoration) {
                member.decorate(makeInstance(member))
            }
        }
    }

    private fun makeInstance(dTarget: Decorated, ann: Annotation? = null): T {
        val targetParams = target.kClass.constructors.asSequence()
            .flatMap { it.lola.getDecoratedParameters<TargetParameter>() }
            .associate { it.target.kParameter to dTarget }
        val annParams = ann?.let {
            target.kClass.constructors.asSequence()
                .flatMap { it.lola.getDecoratedParameters<AnnotationParameter>() }
                .associate { it.target.kParameter to ann }
        }
        return target.createInstance(if (annParams == null) targetParams else (targetParams + annParams))
    }

    private fun getAnnotated(element: KAnnotatedElement): Annotation? {
        return forAnnotated?.let { ann -> element.annotations.firstOrNull { it.annotationClass == ann.annotation } }
    }

    private fun isSubclass(clazz: KClass<*>): Boolean {
        return forSubclasses?.let { clazz.isSubclassOf(it.parent) } ?: false
    }

    class TargetParameter(override val target: LParameter) : Decoration<LParameter>
    class AnnotationParameter(override val target: LParameter) : Decoration<LParameter>
}