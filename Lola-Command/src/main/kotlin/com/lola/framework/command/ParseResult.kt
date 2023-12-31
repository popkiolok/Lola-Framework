package com.lola.framework.command

interface ParseResult

class ParseResultSuccess<T>(val value: T, val argLength: Int) : ParseResult {
    operator fun component1() = value
    operator fun component2() = argLength
}

class ParseResultFailure(val message: () -> String) : ParseResult

class ParseResultMultiError(
    val parsed: Map<ArgumentReference, ParseResultSuccess<*>>,
    val lastFailed: ArgumentReference,
    val messages: Iterable<() -> String>
) : ParseResult

