package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.refType
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class ArgumentEnumFabric : ArgumentParserFabric<ArgumentEnum> {

    override fun canParse(type: KType) = type.jvmErasure.isSubclassOf(Enum::class)

    override fun create(argsContainer: ArgumentsClass, argument: ArgumentReference): ArgumentEnum {
        return ArgumentEnum(argument.target.self.refType.jvmErasure.java.asSubclass(Enum::class.java).enumConstants)
    }
}

class ArgumentEnum(private val constants: Array<out Enum<*>>) : ArgumentWord() {

    override fun parse(pctx: ParsingContext): ParseResult {
        val asWord = super.parseAsString(pctx.input)
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