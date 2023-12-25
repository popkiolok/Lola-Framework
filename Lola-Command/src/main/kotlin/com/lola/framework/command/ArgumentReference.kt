package com.lola.framework.command

import com.lola.framework.core.LAnnotatedElement
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.name
import com.lola.framework.core.refType
import com.lola.framework.core.toJSON
import java.lang.IllegalArgumentException
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

class ArgumentReference(override val target: LAnnotatedElement, private val holder: ArgumentsClass) :
    Decoration<LAnnotatedElement> {
    val required: Boolean
        get() = target.self.let { if (it is KProperty<*>) it.isLateinit else if (it is KParameter) !it.isOptional else throw IllegalArgumentException() }
    val parsers: MutableList<ArgumentParser> = ArrayList()

    init {
        parserAddListeners += this
    }

    fun parse(pctx: ParsingContext): ParseResult {
        val errors = ArrayList<() -> String>()
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
        if (parserFabric.canParse(target.self.refType)) {
            parsers += parserFabric.create(holder, this)
        }
    }

    fun getCompletions(
        argsLeftPart: String,
        isLast: Boolean,
        parsed: Map<ArgumentReference, ParseResultSuccess<*>>
    ): List<String> {
        return sortCompletions(argsLeftPart, parsers.flatMap { it.complete(argsLeftPart, isLast, parsed) })
    }

    override fun toString(): String {
        return "argument ${target.self.name}"
    }
}