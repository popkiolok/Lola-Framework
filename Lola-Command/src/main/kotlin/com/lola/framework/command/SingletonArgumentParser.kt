package com.lola.framework.command

import kotlin.reflect.KType

interface SingletonArgumentParser : ArgumentParser, ArgumentParserFabric<ArgumentParser> {
    override fun create(argsContainer: ArgumentsClass, argument: ArgumentReference): ArgumentParser {
        return this
    }

    override fun create(argumentType: KType): ArgumentParser? {
        return this
    }
}