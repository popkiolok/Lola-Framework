package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.LType

open class ArgumentWord : SingletonArgumentParser {
    override fun canParse(type: LType) = type.clazz == String::class

    override fun parse(pctx: ParsingContext): ParseResult {
        return parseAsString(pctx.argsLeft)
    }

    protected fun parseAsString(argsLeft: String): ParseResultSuccess<String> {
        val word = argsLeft.substringBefore(' ')
        return ParseResultSuccess(word, (word.length + 1).coerceAtMost(argsLeft.length))
    }
}