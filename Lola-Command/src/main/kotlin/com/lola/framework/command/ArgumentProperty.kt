package com.lola.framework.command

import com.lola.framework.core.property.Property
import com.lola.framework.core.property.PropertyDecoration
import com.lola.framework.core.toJSON
import com.lola.framework.setting.setting

class ArgumentProperty(override val self: Property, private val holder: ArgumentsContainer) : PropertyDecoration {
    val required: Boolean
        get() = !self.hasDefaultValue
    val parsers: MutableList<ArgumentParser> = ArrayList()

    init {
        CommandRegistry.parserAddListeners += this
    }

    fun parse(pctx: ParsingContext): ParseResult {
        val errors by lazy { ArrayList<() -> String>() }
        if (parsers.isEmpty()) {
            log.error { "No parser present for argument ${this.toJSON()}." }
        }
        for (parser in parsers) {
            when (val result = parser.parse(pctx)) {
                is ParseResultSuccess<*> -> return result
                is ParseResultFailure -> errors += result.message
                is ParseResultMultiError -> errors += result.messages
            }
        }
        return ParseResultMultiError(pctx.parsed, this, errors)
    }

    fun onParserAdded(parserFabric: ArgumentParserFabric<*>) {
        if (parserFabric.canParse(self.type)) {
            parsers += parserFabric.create(holder, this)
        }
    }

    fun getCompletions(
        argsLeftPart: String,
        isLast: Boolean,
        parsed: Map<ArgumentProperty, ParseResultSuccess<*>>
    ): List<String> {
        return sortCompletions(argsLeftPart, parsers.flatMap { it.complete(argsLeftPart, isLast, parsed) })
    }

    override fun toString(): String {
        return "[ArgumentProperty/${hashCode()}] ${self.setting?.name}"
    }
}