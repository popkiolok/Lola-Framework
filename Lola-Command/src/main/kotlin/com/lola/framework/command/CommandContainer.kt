package com.lola.framework.command

import com.lola.framework.command.arguments.OrderedArgumentSequence
import com.lola.framework.core.container.Container
import com.lola.framework.core.container.decorations.AddPropertyListener
import com.lola.framework.core.property.Property
import com.lola.framework.core.toJSON
import com.lola.framework.module.DependenciesUsingContainer
import com.lola.framework.setting.Setting

class CommandContainer(val name: String, self: Container) : ArgumentsContainer(self) {
    val orderedArgs = OrderedArgumentSequence(this)

    override fun toString() = toJSON()
}