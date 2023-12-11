package com.lola.framework.command.arguments

import com.lola.framework.command.*
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

open class ArgumentWord : SingletonArgumentParser {
    override fun canParse(type: KType) = type.jvmErasure == String::class

    override fun parse(pctx: ParsingContext): ParseResult {
        return parseAsString(pctx.input)
    }

    protected fun parseAsString(argsLeft: String): ParseResultSuccess<String> {
        val word = argsLeft.substringBefore(' ')
        return ParseResultSuccess(word, (word.length + 1).coerceAtMost(argsLeft.length))
    }
}