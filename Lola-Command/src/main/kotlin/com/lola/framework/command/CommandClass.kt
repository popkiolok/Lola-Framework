package com.lola.framework.command

import com.lola.framework.command.arguments.OrderedArgumentSequence
import com.lola.framework.core.LClass
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.getDecoration

class CommandClass(target: LClass<out Runnable>, val data: Command) : ArgumentsClass(target) {
    val orderedArgs by lazy { OrderedArgumentSequence(target.getDecoration<ArgumentsClass>()) }

    override fun toString(): String {
        return "command ${data.name}"
    }
}