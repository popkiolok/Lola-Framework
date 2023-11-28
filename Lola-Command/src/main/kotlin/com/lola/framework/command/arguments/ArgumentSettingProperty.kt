package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.LType
import com.lola.framework.module.ModuleContainer
import com.lola.framework.setting.SettingProperty
import com.lola.framework.setting.settings

class ArgumentSettingPropertyFabric : ArgumentParserFabric<ArgumentSettingProperty> {
    override fun canParse(type: LType) = type.clazz == SettingProperty::class

    override fun create(argsContainer: ArgumentsContainer, argument: ArgumentProperty): ArgumentSettingProperty {
        val index = argsContainer.arguments.indexOf(argument)
        assert(index >= 0)
        val before = argsContainer.arguments.subList(0, index)
        val associatedModuleArg = before.asSequence()
            .filter { arg -> arg.parsers.any { it is ArgumentModuleContainer } }
            .withIndex()
            .minByOrNull { index - it.index }?.value
        if (associatedModuleArg == null) {
            log.error { "No ModuleContainer argument present before SettingProperty argument in arguments list '${before.joinToString()}' for argument '$argument'." }
            throw IllegalStateException()
        }
        return ArgumentSettingProperty(associatedModuleArg)
    }
}

class ArgumentSettingProperty(private val associatedModuleArg: ArgumentProperty) : ArgumentString() {
    override fun canParse(type: LType) = type.clazz == SettingProperty::class

    override fun parse(pctx: ParsingContext): ParseResult {
        val moduleContainer = pctx.parsed[associatedModuleArg]?.value as ModuleContainer
        val asString = super.parseAsString(pctx.argsLeft, pctx.isLast)
        val name = asString.value
        val sett = moduleContainer.self.settings?.settings?.firstOrNull {
            name.equals(it.name, ignoreCase = true) ||
                    name.equals(it.name.replace(" ", ""), ignoreCase = true)
        } ?: run {
            val nameLowCase = name.lowercase()
            val allSettings = moduleContainer.self.settings?.settings?.map { it.name.lowercase() }
            allSettings?.let { bestCompletion(nameLowCase, allSettings) }?.let { s ->
                return ParseResultFailure { "Setting with name '$name' does not exist. Did you mean '$s'?" }
            }
            return ParseResultFailure { "Setting with name '$name' does not exist." }
        }
        return ParseResultSuccess(sett, asString.argLength)
    }

    override fun complete(
        argsLeft: String,
        isLast: Boolean,
        parsed: Map<ArgumentProperty, ParseResultSuccess<*>>
    ): List<String> {
        val moduleContainer = parsed[associatedModuleArg]?.value as ModuleContainer
        val asString = super.parseAsString(argsLeft, isLast)
        return sortCompletions(
            asString.value,
            moduleContainer.self.settings?.settings?.map { it.name } ?: return emptyList())
    }
}