package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.Type
import java.math.BigDecimal
import java.math.BigInteger

class ArgumentByte : ArgumentParser {
    override fun canParse(type: Type) = type.clazz == Byte::class
    override fun parse(argsLeft: String, isLast: Boolean) = parse(argsLeft) { it.toByte() }
}

class ArgumentShort : ArgumentParser {
    override fun canParse(type: Type) = type.clazz == Short::class
    override fun parse(argsLeft: String, isLast: Boolean) = parse(argsLeft) { it.toShort() }
}

class ArgumentInt : ArgumentParser {
    override fun canParse(type: Type) = type.clazz == Int::class
    override fun parse(argsLeft: String, isLast: Boolean) = parse(argsLeft) { it.toInt() }
}

class ArgumentLong : ArgumentParser {
    override fun canParse(type: Type) = type.clazz == Long::class
    override fun parse(argsLeft: String, isLast: Boolean) = parse(argsLeft) { it.toLong() }
}

class ArgumentBigInteger : ArgumentParser {
    override fun canParse(type: Type) = type.clazz == BigInteger::class
    override fun parse(argsLeft: String, isLast: Boolean) = parse(argsLeft) { it.toBigInteger() }
}

class ArgumentFloat : ArgumentParser {
    override fun canParse(type: Type) = type.clazz == Float::class
    override fun parse(argsLeft: String, isLast: Boolean) = parse(argsLeft) { it.toFloat() }
}

class ArgumentDouble : ArgumentParser {
    override fun canParse(type: Type) = type.clazz == Double::class
    override fun parse(argsLeft: String, isLast: Boolean) = parse(argsLeft) { it.toDouble() }
}

class ArgumentBigDecimal : ArgumentParser {
    override fun canParse(type: Type) = type.clazz == BigDecimal::class
    override fun parse(argsLeft: String, isLast: Boolean) = parse(argsLeft) { it.toBigDecimal() }
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