package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.Type

class ArgumentNull : ArgumentWord() {
    private val failureResult = ParseResultFailure { "Argument is not 'null', 'nil' or '-' " }

    override fun canParse(type: Type) = type.nullable

    override fun parse(pctx: ParsingContext): ParseResult {
        val (word, argLength) = super.parseAsString(pctx.argsLeft)
        if (word.equals("-", ignoreCase = true) || word.equals("null", ignoreCase = true) ||
            word.equals("nil", ignoreCase = true)
        )
            return ParseResultSuccess(null, argLength)
        return failureResult
    }
}