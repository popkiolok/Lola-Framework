package com.lola.framework.command

import java.lang.IllegalStateException

interface ParseResult

class ParseResultSuccess(val value: Any?, val argLength: Int) : ParseResult

class  ParseResultFailure(val message: () -> String) : ParseResult

class  ParseResultMultiError(val messages: Iterable<() -> String>) : ParseResult

