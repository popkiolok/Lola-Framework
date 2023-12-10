package com.lola.framework.command

import com.lola.framework.core.refType
import kotlin.reflect.KType

interface ArgumentParserFabric<T : ArgumentParser> {
    fun canParse(type: KType): Boolean

    fun create(argsContainer: ArgumentsClass, argument: ArgumentReference): T {
        return create(argument.target.self.refType)!!
    }

    fun create(argumentType: KType): T? {
        return null
    }
}