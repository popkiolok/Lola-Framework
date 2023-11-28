package com.lola.framework.command

import com.lola.framework.core.LType

interface ArgumentParserFabric<T : ArgumentParser> {
    fun canParse(type: LType): Boolean

    fun create(argsContainer: ArgumentsContainer, argument: ArgumentProperty): T {
        return create(argument.self.type)!!
    }

    fun create(argumentType: LType): T? {
        return null
    }
}