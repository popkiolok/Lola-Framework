package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.constructorsParameters
import com.lola.framework.core.decoration.getDecorations
import com.lola.framework.core.lola
import com.lola.framework.core.refType
import com.lola.framework.module.ModuleClass
import com.lola.framework.setting.SettingReference
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ArgumentSettingPropertyFabric : ArgumentParserFabric<ArgumentSettingReference> {
    override fun canParse(type: KType) = type.jvmErasure == SettingReference::class

    override fun create(argsContainer: ArgumentsClass, argument: ArgumentReference): ArgumentSettingReference {
        val index = argsContainer.arguments.indexOf(argument)
        assert(index >= 0)
        val before = argsContainer.arguments.subList(0, index)
        val associatedModuleArg = before.asSequence()
            .filter { arg -> arg.target.self.refType.jvmErasure == ModuleClass::class }
            .withIndex()
            .minByOrNull { index - it.index }?.value
        if (associatedModuleArg == null) {
            log.error { "No ModuleClass argument present before SettingProperty argument in arguments list '${before.joinToString()}' for argument '$argument'." }
            throw IllegalStateException()
        }
        return ArgumentSettingReference(associatedModuleArg)
    }
}

class ArgumentSettingReference(private val associatedModuleArg: ArgumentReference) : ArgumentString() {
    override fun canParse(type: KType) = type.jvmErasure == SettingReference::class

    override fun parse(pctx: ParsingContext): ParseResult {
        val moduleClass = pctx.parsed[associatedModuleArg]?.value as ModuleClass<*>
        val asString = super.parseAsString(pctx.input, pctx.isLast)
        if (asString is ParseResultFailure) {
            return asString
        }
        val name = (asString as ParseResultSuccess<String>).value
        val settings = moduleClass.target.getDecoratedMembers<SettingReference<*>>().toMutableList()
        moduleClass.target.self.constructorsParameters.mapNotNull {
            it.lola.getDecorations<SettingReference<*>>().firstOrNull()
        }.filter { settings.none { s -> it.data.name == s.data.name } }.forEach { settings += it }
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
        val value = if (asString is ParseResultFailure) "" else (asString as ParseResultSuccess<String>).value
        return sortCompletions(
            value,
            moduleClass.target.getDecoratedMembers<SettingReference<*>>().map { it.data.name }.asIterable()
        )
    }
}