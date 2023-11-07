package com.lola.framework.command

import com.lola.framework.core.Type
import com.lola.framework.core.util.Option
import org.apache.commons.text.similarity.LevenshteinDistance
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass

interface ArgumentParser {

    fun parse(pctx: ParsingContext): ParseResult

    fun complete(argsLeft: String): List<String> {
        return emptyList()
    }

    fun complete(argsLeft: String, isLast: Boolean): List<String> {
        return complete(argsLeft)
    }

    fun complete(argsLeft: String, isLast: Boolean, parsed: Map<ArgumentProperty, ParseResultSuccess<*>>): List<String> {
        return complete(argsLeft, isLast)
    }
}

fun sortCompletions(text: String, variants: Iterable<String>): List<String> {
    return variants.sortedBy { LevenshteinDistance.getDefaultInstance().apply(text.lowercase(), it.lowercase()) }
}

fun bestCompletion(text: String, variants: Iterable<String>): String? {
    return variants.minByOrNull { LevenshteinDistance.getDefaultInstance().apply(text.lowercase(), it.lowercase()) }
}