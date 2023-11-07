package com.lola.framework.command

import com.lola.framework.core.container.Container
import com.lola.framework.core.container.decorations.AddPropertyListener
import com.lola.framework.core.property.Property
import com.lola.framework.core.toJSON
import com.lola.framework.module.DependenciesUsingContainer
import com.lola.framework.setting.Setting

open class ArgumentsContainer(override val self: Container) : AddPropertyListener, DependenciesUsingContainer {
    val arguments: List<ArgumentProperty> = ArrayList()

    override fun onPropertyAdded(property: Property) {
        super.onPropertyAdded(property)
        if (property.annotations.hasAnnotation(Setting::class)) {
            val arg = ArgumentProperty(property, this)
            arguments as ArrayList += arg
            CommandRegistry.parserFabrics.forEach { arg.onParserAdded(it) }
            property.decorate(arg)
        }
    }

    override fun toString() = toJSON()
}