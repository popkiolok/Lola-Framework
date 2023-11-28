package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.LType
import kotlin.reflect.full.isSubclassOf

class ArgumentEnumFabric : ArgumentParserFabric<ArgumentEnum> {

    override fun canParse(type: LType): Boolean {
        return type.clazz.isSubclassOf(Enum::class)
    }

    override fun create(argsContainer: ArgumentsContainer, argument: ArgumentProperty): ArgumentEnum {
        return ArgumentEnum(argument.self.type.clazz.java.asSubclass(Enum::class.java).enumConstants)
    }
}

class ArgumentEnum(private val constants: Array<out Enum<*>>) : ArgumentWord() {

    override fun parse(pctx: ParsingContext): ParseResult {
        val asWord = super.parseAsString(pctx.argsLeft)
        return constants.find { it.name.equals(asWord.value, ignoreCase = true) }.let { enumValue ->
            if (enumValue == null) {
                ParseResultFailure { "Variant '${asWord.value}' does not exist. Allowed variants: '${constants.joinToString { it.name }}'." }
            } else {
                ParseResultSuccess(enumValue, asWord.argLength)
            }
        }
    }

    override fun complete(argsLeft: String): List<String> {
        val asWord = super.parseAsString(argsLeft)
        return sortCompletions(asWord.value, constants.map { it.name })
    }
}