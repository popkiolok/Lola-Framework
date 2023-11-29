package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.LType
import com.lola.framework.core.annotation.hasAnnotation
import com.lola.framework.core.decoration.ResolveClassListener
import com.lola.framework.core.LClass
import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.container.context.Context
import com.lola.framework.core.container.subscribeAddContainerListener
import com.lola.framework.core.decoration.getDecoration
import com.lola.framework.core.decoration.hasDecoration
import java.lang.IllegalArgumentException

class OrderedArgumentSequenceFabric : ArgumentParserFabric<OrderedArgumentSequence>, ResolveClassListener {

    init {
        subscribeAddContainerListener(this)
    }

    override fun canParse(type: LType) = type.clazz?.let {
        it.hasDecoration<ArgumentsContainer>() || it.implementations.any { imp -> imp.hasDecoration<ArgumentsContainer>() }
    } ?: false

    override fun create(argumentType: LType): OrderedArgumentSequence {
        return OrderedArgumentSequence(argumentType.clazz!!.let {
            it.getDecoration<ArgumentsContainer>()
                ?: it.implementations.first { imp -> imp.hasDecoration<ArgumentsContainer>() }
                    .getDecoration<ArgumentsContainer>()
        }!!)
    }

    override fun onClassFound(container: LClass) {
        if (container.hasAnnotation<ComplexArgument>()) {
            container.decorate(ArgumentsContainer(container))
        }
    }
}

class OrderedArgumentSequence(private val argsContainer: ArgumentsContainer) : ArgumentParser {

    override fun parse(pctx: ParsingContext): ParseResult {
        val startWithBracket = pctx.argsLeft.startsWith('(')
        val resultAsMap = if (pctx.isLast && !startWithBracket) {
            parseAsMap(pctx.argsLeft, pctx.context)
        } else {
            if (startWithBracket && pctx.argsLeft.contains(')')) {
                val argStr = extractBracketsContent(pctx.argsLeft, '(', ')')
                parseAsMap(argStr, pctx.context)
            } else {
                return ParseResultFailure { "OrderedArgumentSequence as argument not last in arguments list should be in '(' ')'." }
            }
        }
        return if (resultAsMap is ParseResultSuccess<*>) {
            val ci = createComplexArgObj(
                resultAsMap as ParseResultSuccess<Map<ArgumentProperty, ParseResultSuccess<*>>>,
                pctx
            )
            ParseResultSuccess(ci.instance, resultAsMap.argLength + (if (pctx.isLast) 0 else 2))
        } else resultAsMap
    }

    private fun createComplexArgObj(
        resultAsMap: ParseResultSuccess<Map<ArgumentProperty, ParseResultSuccess<*>>>,
        pctx: ParsingContext
    ): ContainerInstance {
        val map = resultAsMap.value
        val propValues = map.mapKeys { it.key.self }.mapValues { it.value.value }
        val paramValues =
            propValues.flatMap { (prop, value) -> prop.parameters.map { it to value } }.toMap()
        val ci = argsContainer.self.createInstance(paramValues, propValues, ctxInitializer = { ctx ->
            ctx.parents += pctx.context
        })
        return ci
    }

    fun parseAsMap(argsStr: String, context: Context): ParseResult {
        if (argsContainer.arguments.isEmpty()) {
            return ParseResultSuccess<Map<ArgumentProperty, Any?>>(emptyMap(), argsStr.length)
        }

        try {
            var args = argsContainer.arguments
            var error: ParseResultMultiError? = null
            do {
                val parsed = HashMap<ArgumentProperty, ParseResultSuccess<*>>()
                var argsLeft = argsStr
                var failed = false

                val lastIndex = args.lastIndex
                for (argIndex in args.indices) {
                    val arg = args[argIndex]
                    val isLast = argIndex == lastIndex
                    val parseResult = arg.parse(ParsingContext(argsLeft, isLast, parsed, context))
                    if (parseResult is ParseResultSuccess<*>) {
                        if (!isLast) {
                            argsLeft = argsLeft.substring(parseResult.argLength)
                        }
                        parsed[arg] = parseResult
                        log.trace { "Parsed argument ${parseResult.value}." }
                    } else if (parseResult is ParseResultMultiError) {
                        if (error == null || error.parsed.size < parseResult.parsed.size) {
                            error = parseResult
                        }
                        failed = true
                        break
                    } else throw IllegalStateException()
                }
                if (!failed) {
                    return ParseResultSuccess<Map<ArgumentProperty, Any?>>(parsed, argsStr.length)
                }
            } while (
                args.indexOfLast { !it.required }.let { lastNotReq ->
                    if (lastNotReq == -1) {
                        false
                    } else {
                        val newArgs = ArrayList<ArgumentProperty>()
                        for (i in 0..<lastNotReq)
                            newArgs += args[i]
                        for (i in (lastNotReq + 1)..<args.size)
                            newArgs += args[i]
                        args = newArgs
                        true
                    }
                }
            )
            return error!!
        } catch (e: Throwable) {
            return ParseResultFailure { "An exception were thrown while parsing arguments: '${e.message}'." }.also { e.printStackTrace() }
        }
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ComplexArgument

fun extractBracketsContent(strInBrackets: String, openingBracket: Char, closingBracket: Char): String {
    var brCount = 1
    for (i in 1..<strInBrackets.length) {
        val c = strInBrackets[i]
        if (c == openingBracket) brCount++ else if (c == closingBracket) brCount--
        if (brCount == 0) {
            return strInBrackets.substring(1, i)
        }
    }
    throw IllegalArgumentException()
}