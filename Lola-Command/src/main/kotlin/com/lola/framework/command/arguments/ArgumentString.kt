package com.lola.framework.command.arguments

import com.lola.framework.command.*
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

open class ArgumentString : SingletonArgumentParser {
    override fun canParse(type: KType) = type.jvmErasure == String::class

    override fun parse(pctx: ParsingContext): ParseResult {
        return parseAsString(pctx.input, pctx.isLast)
    }

    protected fun parseAsString(argsLeft: String, isLast: Boolean): ParseResult {
        if (argsLeft.startsWith('"')) {
            val str = argsLeft.substring(1).substringBefore('"')
            return ParseResultSuccess(str, (str.length + 3).coerceAtMost(argsLeft.length))
        } else if (isLast) {
            if (argsLeft.isEmpty()) {
                return ParseResultFailure { "Expected string value but found nothing. Use \"\" for empty string." }
            }
            return ParseResultSuccess(argsLeft, argsLeft.length)
        }
        val word = argsLeft.substringBefore(' ')
        return ParseResultSuccess(word, (word.length + 1).coerceAtMost(argsLeft.length))
    }
}