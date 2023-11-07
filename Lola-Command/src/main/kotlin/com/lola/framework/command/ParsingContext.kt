package com.lola.framework.command

import com.lola.framework.core.container.context.Context

class ParsingContext(
    val argsLeft: String,
    val isLast: Boolean,
    val parsed: Map<ArgumentProperty, ParseResultSuccess<*>>,
    val context: Context
)