package com.lola.framework.command.arguments

import com.lola.framework.command.*
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ArgumentByte : SingletonArgumentParser {
    override fun canParse(type: KType) = type.jvmErasure == Byte::class
    override fun parse(pctx: ParsingContext) = parse(pctx.argsLeft) { it.toByte() }
}

class ArgumentShort : SingletonArgumentParser {
    override fun canParse(type: KType) = type.jvmErasure == Short::class
    override fun parse(pctx: ParsingContext) = parse(pctx.argsLeft) { it.toShort() }
}

class ArgumentInt : SingletonArgumentParser {
    override fun canParse(type: KType) = type.jvmErasure == Int::class
    override fun parse(pctx: ParsingContext) = parse(pctx.argsLeft) { it.toInt() }
}

class ArgumentLong : SingletonArgumentParser {
    override fun canParse(type: KType) = type.jvmErasure == Long::class
    override fun parse(pctx: ParsingContext) = parse(pctx.argsLeft) { it.toLong() }
}

class ArgumentBigInteger : SingletonArgumentParser {
    override fun canParse(type: KType) = type.jvmErasure == BigInteger::class
    override fun parse(pctx: ParsingContext) = parse(pctx.argsLeft) { it.toBigInteger() }
}

class ArgumentFloat : SingletonArgumentParser {
    override fun canParse(type: KType) = type.jvmErasure == Float::class
    override fun parse(pctx: ParsingContext) = parse(pctx.argsLeft) { it.toFloat() }
}

class ArgumentDouble : SingletonArgumentParser {
    override fun canParse(type: KType) = type.jvmErasure == Double::class
    override fun parse(pctx: ParsingContext) = parse(pctx.argsLeft) { it.toDouble() }
}

class ArgumentBigDecimal : SingletonArgumentParser {
    override fun canParse(type: KType) = type.jvmErasure == BigDecimal::class
    override fun parse(pctx: ParsingContext) = parse(pctx.argsLeft) { it.toBigDecimal() }
}

private inline fun parse(argsLeft: String, parser: (String) -> Number): ParseResult {
    val substr = argsLeft.substringBefore(' ')
    return try {
        val result = parser(substr)
        ParseResultSuccess(result, (substr.length + 1).coerceAtMost(argsLeft.length))
    } catch (t: Throwable) {
        ParseResultFailure { t.message ?: "Unable to parse number." }
    }
}