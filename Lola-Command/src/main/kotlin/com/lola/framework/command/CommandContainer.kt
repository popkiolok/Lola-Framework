package com.lola.framework.command

import com.lola.framework.command.arguments.OrderedArgumentSequence
import com.lola.framework.core.LClass
import com.lola.framework.core.toJSON

class CommandContainer(val name: String, self: LClass) : ArgumentsContainer(self) {
    val orderedArgs = OrderedArgumentSequence(this)

    override fun toString() = toJSON()
}