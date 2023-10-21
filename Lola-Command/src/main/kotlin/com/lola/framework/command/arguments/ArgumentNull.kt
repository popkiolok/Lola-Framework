package com.lola.framework.command.arguments

import com.lola.framework.command.ArgumentParser
import com.lola.framework.command.ParseResult
import com.lola.framework.command.ParseResultFailure
import com.lola.framework.command.ParseResultSuccess
import com.lola.framework.core.Type

class ArgumentNull : ArgumentParser {
    private val failureResult = ParseResultFailure { "Argument is not 'null', 'nil' or '-' " }

    override fun canParse(type: Type) = type.nullable

    override fun parse(argsLeft: String, isLast: Boolean): ParseResult {
        val word = argsLeft.substringBefore(' ')
        if (word.equals("-", ignoreCase = true) || word.equals("null", ignoreCase = true) ||
            word.equals("nil", ignoreCase = true)
        )
            return ParseResultSuccess(null, (word.length + 1).coerceAtMost(argsLeft.length))
        return failureResult
    }
}