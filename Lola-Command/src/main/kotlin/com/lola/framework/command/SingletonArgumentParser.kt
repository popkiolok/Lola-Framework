package com.lola.framework.command

interface SingletonArgumentParser : ArgumentParser, ArgumentParserFabric<ArgumentParser> {
    override fun create(argsContainer: ArgumentsContainer, argument: ArgumentProperty): ArgumentParser {
        return this
    }
}