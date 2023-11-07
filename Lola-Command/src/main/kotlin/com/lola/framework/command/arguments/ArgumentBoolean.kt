package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.Type

class ArgumentBoolean : SingletonArgumentParser {
    private val failureResult = ParseResultFailure { "Argument is not '+'/'-', 'yes'/'no', 'y'/'n' or 'true'/'false'." }

    override fun canParse(type: Type) = type.clazz == Boolean::class

    override fun parse(pctx: ParsingContext): ParseResult {
        val word = pctx.argsLeft.substringBefore(' ')
        return if (yesVariants.any { word.equals(it, ignoreCase = true) }) {
            ParseResultSuccess(true, (word.length + 1).coerceAtMost(pctx.argsLeft.length))
        } else if (noVariants.any { word.equals(it, ignoreCase = true) }) {
            ParseResultSuccess(false, (word.length + 1).coerceAtMost(pctx.argsLeft.length))
        } else failureResult
    }

    override fun complete(argsLeft: String): List<String> {
        return sortCompletions(argsLeft.substringBefore(' '), allVariants)
    }

    companion object {
        private val yesVariants = arrayOf("+", "yes", "y", "true")
        private val noVariants = arrayOf("-", "no", "n", "false")
        private val allVariants = listOf(*(yesVariants + noVariants))
    }
}