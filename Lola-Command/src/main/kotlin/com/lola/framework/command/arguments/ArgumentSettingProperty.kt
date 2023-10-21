package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.Type
import com.lola.framework.module.ModuleContainer
import com.lola.framework.setting.SettingProperty
import com.lola.framework.setting.settings

class ArgumentSettingProperty(private val moduleContainer: ModuleContainer) : ArgumentString() {
    override fun canParse(type: Type) = type.clazz == SettingProperty::class

    override fun parse(argsLeft: String, isLast: Boolean): ParseResult {
        val asString = (super.parse(argsLeft, isLast) as ParseResultSuccess)
        val name = asString.value as String
        val sett = moduleContainer.self.settings?.settings?.firstOrNull {
            name.equals(it.name, ignoreCase = true) ||
                    name.equals(it.name.replace(" ", ""), ignoreCase = true)
        } ?: run {
            val nameLowCase = name.lowercase()
            val allSettings = moduleContainer.self.settings?.settings?.map { it.name.lowercase() }
            allSettings?.let { bestCompletion(nameLowCase, allSettings) }?.let { s ->
                return ParseResultFailure { "Setting with name '§o$name§c' does not exist. Did you mean '§o$s§d'?" }
            }
            return ParseResultFailure { "Setting with name '§o$name§c' does not exist." }
        }
        return ParseResultSuccess(sett, asString.argLength)
    }
}