package com.lola.framework.command.arguments

import com.lola.framework.command.ArgumentParser
import com.lola.framework.command.ParseResult
import com.lola.framework.command.ParseResultSuccess
import com.lola.framework.core.Type

open class ArgumentString : ArgumentParser {
    override fun canParse(type: Type) = type.clazz == String::class

    override fun parse(argsLeft: String, isLast: Boolean): ParseResult {
        if (isLast) {
            return ParseResultSuccess(argsLeft, argsLeft.length)
        } else if (argsLeft.startsWith('"')) {
            val matcher = stringPattern.find(argsLeft)
            if (matcher != null) {
                return ParseResultSuccess(matcher.groups[1], (matcher.value.length + 1).coerceAtMost(argsLeft.length))
            }
        }
        val word = argsLeft.substringBefore(' ')
        return ParseResultSuccess(word, (word.length + 1).coerceAtMost(argsLeft.length))
    }

    companion object {
        private val stringPattern = Regex("\"(.*?)\"")
    }
}