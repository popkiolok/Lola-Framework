package com.lola.framework.command

import com.lola.framework.core.Type
import java.lang.UnsupportedOperationException

interface ArgumentParserFabric<T : ArgumentParser> {
    fun canParse(type: Type): Boolean

    fun create(argsContainer: ArgumentsContainer, argument: ArgumentProperty): T {
        return create(argument.self.type)!!
    }

    fun create(argumentType: Type): T? {
        return null
    }
}