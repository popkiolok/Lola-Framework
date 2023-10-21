package com.lola.framework.command

import com.lola.framework.core.container.Container
import com.lola.framework.core.container.decorations.AddPropertyListener
import com.lola.framework.core.property.Property
import com.lola.framework.core.toJSON
import com.lola.framework.module.DependenciesUsingContainer
import com.lola.framework.setting.Setting
import com.lola.framework.setting.SettingProperty
import com.lola.framework.setting.setting
import java.lang.IllegalStateException

class CommandContainer(val name: String, override val self: Container) : AddPropertyListener,
    DependenciesUsingContainer {
    val arguments: List<ArgumentProperty>
        get() = args0

    private val args0: MutableList<ArgumentProperty> = ArrayList()

    override fun onPropertyAdded(property: Property) {
        super.onPropertyAdded(property)
        if (property.annotations.hasAnnotation(Setting::class)) {
            val arg = ArgumentProperty(property)
            CommandRegistry.argumentParsers.forEach { arg.onParserAdded(it) }
            property.decorate(arg)
            args0 += arg
        }
    }

    override fun toString() = toJSON()
}