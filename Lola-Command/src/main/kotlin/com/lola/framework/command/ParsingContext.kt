package com.lola.framework.command

import com.lola.framework.core.context.Context

class ParsingContext(
    val input: String,
    val isLast: Boolean,
    val parsed: Map<ArgumentReference, ParseResultSuccess<*>>,
    val context: Context
)