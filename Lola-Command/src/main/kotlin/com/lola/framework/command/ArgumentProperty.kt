package com.lola.framework.command

import com.lola.framework.core.property.Property
import com.lola.framework.core.property.PropertyDecoration
import com.lola.framework.core.toJSON

class ArgumentProperty(override val self: Property) : PropertyDecoration {
    val required: Boolean
        get() = !self.hasDefaultValue
    private val parsers: MutableList<ArgumentParser> = ArrayList()

    fun parse(argsLeft: String, isLast: Boolean): ParseResult {
        val errors = ArrayList<() -> String>()
        if (parsers.isEmpty()) {
            log.error { "No parser present for argument ${this.toJSON()}." }
        }
        for (parser in parsers) {
            when (val result = parser.parse(argsLeft, isLast)) {
                is ParseResultSuccess -> return result
                is ParseResultFailure -> errors += result.message
                is ParseResultMultiError -> errors += result.messages
            }
        }
        return ParseResultMultiError(errors)
    }

    fun onParserAdded(parser: ArgumentParser) {
        if (parser.canParse(self.type)) {
            parsers += parser
        }
    }
}