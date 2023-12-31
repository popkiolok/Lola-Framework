package com.lola.framework.command.arguments

import com.lola.framework.command.*
import kotlin.reflect.KType

class ArgumentNull : ArgumentWord() {
    private val failureResult = ParseResultFailure { "Argument is not 'null', 'nil' or '-' " }

    override fun canParse(type: KType) = type.isMarkedNullable

    override fun parse(pctx: ParsingContext): ParseResult {
        val (word, argLength) = super.parseAsString(pctx.input)
        if (word.equals("-", ignoreCase = true) || word.equals("null", ignoreCase = true) ||
            word.equals("nil", ignoreCase = true)
        )
            return ParseResultSuccess(null, argLength)
        return failureResult
    }
}