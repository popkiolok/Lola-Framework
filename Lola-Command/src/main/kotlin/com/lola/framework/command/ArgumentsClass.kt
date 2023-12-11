package com.lola.framework.command

import com.lola.framework.core.*
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.setting.Setting
import java.util.PriorityQueue
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*

open class ArgumentsClass(final override val target: LClass<*>) : Decoration<LClass<*>> {
    val arguments: List<ArgumentReference>

    init {
        val args = ArrayList<ArgumentReference>()
        val sortedProps = PriorityQueue<Pair<Int, KProperty<*>>>(compareByDescending { it.first })
        target.self.superclasses.forEach {
            addOrderedArgProps(it, dest = sortedProps)
        }
        sortedProps.forEach { args += ArgumentReference(it.second.lola, this) }
        target.self.constructorsParameters.forEach { kParam ->
            if (kParam.hasAnnotation<Setting>()) args += ArgumentReference(kParam.lola, this)
        }
        target.self.declaredMemberProperties.forEach {
            val ann = it.findAnnotation<Setting>()
            if (ann != null && args.none { arg ->
                    arg.target is LParameter && arg.target.self.findAnnotation<Setting>()!!.name == ann.name
                }) {
                args += ArgumentReference(it.lola, this)
            }
        }
        arguments = args.filter { a ->
            args.none { b ->
                a !== b && a.target.self.findAnnotation<Setting>()!!.name == b.target.self.findAnnotation<Setting>()!!.name
            }
        }
        arguments.forEach {
            it.target.decorate(it)
            parserFabrics.forEach { p -> it.onParserAdded(p) }
        }
    }

    private fun addOrderedArgProps(kClass: KClass<*>, depth: Int = 0, dest: PriorityQueue<Pair<Int, KProperty<*>>>) {
        kClass.superclasses.forEach { addOrderedArgProps(it, depth + 1, dest) }
        kClass.declaredMemberProperties.forEach { if (it.hasAnnotation<Setting>()) dest += depth to it }
    }

    override fun toString() = toJSON()
}