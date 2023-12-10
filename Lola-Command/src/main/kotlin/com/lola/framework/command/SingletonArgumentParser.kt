package com.lola.framework.command

interface SingletonArgumentParser : ArgumentParser, ArgumentParserFabric<ArgumentParser> {
    override fun create(argsContainer: ArgumentsClass, argument: ArgumentReference): ArgumentParser {
        return this
    }
}