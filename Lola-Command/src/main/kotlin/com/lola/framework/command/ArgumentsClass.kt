package com.lola.framework.command

import com.lola.framework.core.*
import com.lola.framework.core.decoration.ResolvePropertyListener
import com.lola.framework.setting.Setting
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

open class ArgumentsClass(final override val target: LClass<*>) : ResolvePropertyListener<LClass<*>> {
    val arguments: List<ArgumentReference> = ArrayList()

    init {
        target.self.constructorsParameters.forEach { kParam ->
            if (kParam.hasAnnotation<Setting>()) {
                val param = kParam.lola
                val arg = ArgumentReference(param, this)
                arguments as ArrayList += arg
                parserFabrics.forEach { arg.onParserAdded(it) }
                param.decorate(arg)
            }
        }
    }

    override fun <R> onPropertyFound(property: LCallable<R, KProperty<R>>) {
        val ann = property.self.findAnnotation<Setting>()
        if (ann != null && target.self.constructorsParameters.none { it.findAnnotation<Setting>()?.name == ann.name }) {
            val arg = ArgumentReference(property, this)
            arguments as ArrayList += arg
            parserFabrics.forEach { arg.onParserAdded(it) }
            property.decorate(arg)
        }
    }

    override fun toString() = toJSON()
}