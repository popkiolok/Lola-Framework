package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.module.ModuleClass
import com.lola.framework.setting.SettingProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ArgumentSettingPropertyFabric : ArgumentParserFabric<ArgumentSettingProperty> {
    override fun canParse(type: KType) = type.jvmErasure == SettingProperty::class

    override fun create(argsContainer: ArgumentsClass, argument: ArgumentReference): ArgumentSettingProperty {
        val index = argsContainer.arguments.indexOf(argument)
        assert(index >= 0)
        val before = argsContainer.arguments.subList(0, index)
        val associatedModuleArg = before.asSequence()
            .filter { arg -> arg.parsers.any { it is ArgumentModuleClass } }
            .withIndex()
            .minByOrNull { index - it.index }?.value
        if (associatedModuleArg == null) {
            log.error { "No ModuleContainer argument present before SettingProperty argument in arguments list '${before.joinToString()}' for argument '$argument'." }
            throw IllegalStateException()
        }
        return ArgumentSettingProperty(associatedModuleArg)
    }
}

class ArgumentSettingProperty(private val associatedModuleArg: ArgumentReference) : ArgumentString() {
    override fun canParse(type: KType) = type.jvmErasure == SettingProperty::class

    override fun parse(pctx: ParsingContext): ParseResult {
        val moduleClass = pctx.parsed[associatedModuleArg]?.value as ModuleClass<*>
        val asString = super.parseAsString(pctx.argsLeft, pctx.isLast)
        val name = asString.value
        val settings = moduleClass.target.getDecoratedMembers<SettingProperty>()
        val sett = settings.firstOrNull {
            name.equals(it.data.name, ignoreCase = true) ||
                    name.equals(it.data.name.replace(" ", ""), ignoreCase = true)
        } ?: run {
            val nameLowCase = name.lowercase()
            val allSettings = settings.map { it.data.name.lowercase() }.toList()
            if (allSettings.isNotEmpty()) {
                bestCompletion(nameLowCase, allSettings).let { s ->
                    return ParseResultFailure { "Setting with name '$name' does not exist. Did you mean '$s'?" }
                }
            }
            return ParseResultFailure { "Setting with name '$name' does not exist." }
        }
        return ParseResultSuccess(sett, asString.argLength)
    }

    override fun complete(
        argsLeft: String,
        isLast: Boolean,
        parsed: Map<ArgumentReference, ParseResultSuccess<*>>
    ): List<String> {
        val moduleClass = parsed[associatedModuleArg]?.value as ModuleClass<*>
        val asString = super.parseAsString(argsLeft, isLast)
        return sortCompletions(asString.value,
            moduleClass.target.getDecoratedMembers<SettingProperty>().map { it.data.name }.asIterable())
    }
}