package com.lola.framework.command.arguments

import com.lola.framework.command.*
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ArgumentBoolean : SingletonArgumentParser {
    private val failureResult = ParseResultFailure { "Argument is not '+'/'-', 'yes'/'no', 'y'/'n' or 'true'/'false'." }

    override fun canParse(type: KType) = type.jvmErasure == Boolean::class

    override fun parse(pctx: ParsingContext): ParseResult {
        val word = pctx.input.substringBefore(' ')
        return if (yesVariants.any { word.equals(it, ignoreCase = true) }) {
            ParseResultSuccess(true, (word.length + 1).coerceAtMost(pctx.input.length))
        } else if (noVariants.any { word.equals(it, ignoreCase = true) }) {
            ParseResultSuccess(false, (word.length + 1).coerceAtMost(pctx.input.length))
        } else failureResult
    }

    override fun complete(argsLeft: String): List<String> {
        return sortCompletions(argsLeft.substringBefore(' '), allVariants)
    }
}

private val yesVariants = arrayOf("+", "yes", "y", "true")
private val noVariants = arrayOf("-", "no", "n", "false")
private val allVariants = listOf(*(yesVariants + noVariants))