package com.lola.framework.command.arguments

import com.lola.framework.command.ParseResult
import com.lola.framework.command.ParseResultSuccess
import com.lola.framework.command.ParsingContext
import com.lola.framework.command.SingletonArgumentParser
import com.lola.framework.core.LType

open class ArgumentString : SingletonArgumentParser {
    override fun canParse(type: LType) = type.clazz == String::class

    override fun parse(pctx: ParsingContext): ParseResult {
        return parseAsString(pctx.argsLeft, pctx.isLast)
    }

    protected fun parseAsString(argsLeft: String, isLast: Boolean): ParseResultSuccess<String> {
        if (argsLeft.startsWith('"')) {
            val str = argsLeft.substring(1).substringBefore('"')
            return ParseResultSuccess(str, (str.length + 3).coerceAtMost(argsLeft.length))
        } else if (isLast) {
            return ParseResultSuccess(argsLeft, argsLeft.length)
        }
        val word = argsLeft.substringBefore(' ')
        return ParseResultSuccess(word, (word.length + 1).coerceAtMost(argsLeft.length))
    }
}