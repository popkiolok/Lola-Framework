package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.*
import com.lola.framework.core.decoration.ResolveClassListener
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.getDecoration
import com.lola.framework.core.decoration.getDecorations
import com.lola.framework.core.decoration.hasDecoration
import java.lang.IllegalArgumentException
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class OrderedArgumentSequenceFabric(override val target: Lola = Lola) : ArgumentParserFabric<OrderedArgumentSequence>,
    ResolveClassListener<Lola> {

    override fun canParse(type: KType) = type.jvmErasure.lola.let {
        it.hasDecoration<ArgumentsClass>() || classes.any { imp -> imp.self.isSubclassOf(it.self) && imp.hasDecoration<ArgumentsClass>() }
    }

    override fun create(argumentType: KType): OrderedArgumentSequence {
        return OrderedArgumentSequence(argumentType.jvmErasure.let {
            it.lola.getDecorations<ArgumentsClass>().firstOrNull()
                ?: classes.first { imp -> imp.self.isSubclassOf(it) && imp.hasDecoration<ArgumentsClass>() }
                    .getDecoration<ArgumentsClass>()
        })
    }

    override fun <T : Any> onClassFound(clazz: LClass<T>) {
        if (clazz.self.hasAnnotation<ComplexArgument>()) {
            clazz.decorate(ArgumentsClass(clazz))
        }
    }
}

class OrderedArgumentSequence(private val argsContainer: ArgumentsClass) : ArgumentParser {

    override fun parse(pctx: ParsingContext): ParseResult {
        val startWithBracket = pctx.input.startsWith('(')
        val resultAsMap = if (pctx.isLast && !startWithBracket) {
            parseAsMap(pctx.input, pctx.context)
        } else {
            if (startWithBracket && pctx.input.contains(')')) {
                val argStr = extractBracketsContent(pctx.input, '(', ')')
                parseAsMap(argStr, pctx.context)
            } else {
                return ParseResultFailure { "OrderedArgumentSequence as argument not last in arguments list should be in '(' ')'." }
            }
        }
        return if (resultAsMap is ParseResultSuccess<*>) {
            val ci = createComplexArgObj(
                resultAsMap as ParseResultSuccess<Map<ArgumentReference, ParseResultSuccess<*>>>,
                pctx
            )
            ParseResultSuccess(ci, resultAsMap.argLength + (if (pctx.isLast) 0 else 2))
        } else resultAsMap
    }

    private fun createComplexArgObj(
        resultAsMap: ParseResultSuccess<Map<ArgumentReference, ParseResultSuccess<*>>>,
        pctx: ParsingContext
    ): Any {
        val (params, props) = resultAsMap.value.entries.partition { it.key.target is LParameter }
        val propValues = props.associate { it.key.target.self as KProperty<*> to it.value.value }
        val paramValues = params.associate { it.key.target.self as KParameter to it.value.value }
        val inst = argsContainer.target.createInstance(paramValues, propValues, ctxInitializer = { ctx ->
            ctx.parents += pctx.context
        })
        return inst
    }

    fun parseAsMap(argsStr: String, context: Context): ParseResult {
        if (argsContainer.arguments.isEmpty()) {
            return ParseResultSuccess<Map<ArgumentReference, Any?>>(emptyMap(), argsStr.length)
        }

        try {
            var args = argsContainer.arguments
            var error: ParseResultMultiError? = null
            do {
                val parsed = HashMap<ArgumentReference, ParseResultSuccess<*>>()
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
                        log.trace { "Parsed $arg as ${parseResult.value}." }
                    } else if (parseResult is ParseResultMultiError) {
                        if (error == null || error.parsed.size < parseResult.parsed.size) {
                            error = parseResult
                        }
                        failed = true
                        break
                    } else throw IllegalStateException()
                }
                if (!failed) {
                    return ParseResultSuccess<Map<ArgumentReference, Any?>>(parsed, argsStr.length)
                }
            } while (
                args.indexOfLast { !it.required }.let { lastNotReq ->
                    if (lastNotReq == -1) {
                        false
                    } else {
                        val newArgs = ArrayList<ArgumentReference>()
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