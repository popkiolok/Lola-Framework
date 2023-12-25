package com.lola.framework.command

import com.lola.framework.core.*
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.util.equalsBy
import java.util.PriorityQueue
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

open class ArgumentsClass(final override val target: LClass<*>) : Decoration<LClass<*>> {
    val arguments: List<ArgumentReference>

    init {
        val args = ArrayList<KAnnotatedElement>()
        val sortedProps = PriorityQueue<Pair<Int, KProperty<*>>>(compareByDescending { it.first })
        target.self.superclasses.forEach {
            addOrderedArgProps(it, dest = sortedProps)
        }
        sortedProps.forEach { args += it.second }
        target.self.constructorsParameters.forEach { kParam ->
            if (kParam.hasAnnotation<Param>()) args += kParam
        }
        target.self.declaredMemberProperties.forEach {
            val ann = it.findAnnotation<Param>()
            if (ann != null && args.none { arg -> arg is KParameter && arg.findAnnotation<Param>()!!.name == ann.name }) {
                args += it
            }
        }
        val targetProps = target.self.memberProperties
        arguments = args.filter { a ->
            args.none { b ->
                a !== b && equalsBy(a, b) { it.findAnnotation<Param>()!!.name }
            }
        }.map { inherited ->
            val argElem = when (inherited) {
                is KParameter -> inherited.lola
                is KProperty<*> -> (targetProps.firstOrNull { it.name == inherited.name } ?: inherited).lola
                else -> throw IllegalArgumentException()
            }
            ArgumentReference(argElem, this)
        }
        arguments.forEach {
            it.target.decorate(it)
            parserFabrics.forEach { p -> it.onParserAdded(p) }
        }
    }

    private fun addOrderedArgProps(kClass: KClass<*>, depth: Int = 0, dest: PriorityQueue<Pair<Int, KProperty<*>>>) {
        kClass.superclasses.forEach { addOrderedArgProps(it, depth + 1, dest) }
        kClass.declaredMemberProperties.forEach { if (it.hasAnnotation<Param>()) dest += depth to it }
    }
}