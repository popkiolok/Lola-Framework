package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.command.arguments.ArgumentString
import com.lola.framework.core.Type
import com.lola.framework.module.ModuleContainer
import com.lola.framework.module.ModuleRegistry
import org.apache.commons.lang3.StringUtils
import kotlin.math.min

class ArgumentModuleContainer : ArgumentString() {

    override fun canParse(type: Type) = type.clazz == ModuleContainer::class

    override fun parse(argsLeft: String, isLast: Boolean): ParseResult {
        val asString = (super.parse(argsLeft, isLast) as ParseResultSuccess)
        val name = asString.value as String
        val module = ModuleRegistry.modulesByName[name]
            ?: ModuleRegistry.modulesByName.entries.firstOrNull { (n, _) ->
                name.equals(n, ignoreCase = true) || name.equals(n.replace(" ", ""), ignoreCase = true)
            }?.value ?: ModuleRegistry.modules.firstOrNull {
                it.path.toString().equals(name, ignoreCase = true) ||
                        it.path.toString().replace(" ", "").equals(name, ignoreCase = true)
            }
            ?: run {
                val allModules = ModuleRegistry.modulesByName.entries.flatMap { (n, m) ->
                    listOf(n.lowercase(), m.path.toString().lowercase())
                }
                val nameLowCase = name.lowercase()
                bestCompletion(nameLowCase, allModules)?.let { nearest ->
                    return ParseResultFailure { "Module with name '§o$name§c' does not exist. Did you mean '§o$nearest§d'?" }
                }
                return ParseResultFailure { "Module with name '§o$name§c' does not exist." }
            }
        return ParseResultSuccess(module, asString.argLength)
    }

    override fun complete(argsLeft: String, isLast: Boolean): List<String> {
        val asString = (super.parse(argsLeft, isLast) as ParseResultSuccess)
        val name = asString.value as String
        val allModules = ModuleRegistry.modulesByName.entries.flatMap { (n, m) ->
            listOf(n.lowercase(), m.path.toString().lowercase())
        }
        val nameLowCase = name.lowercase()
        return sortCompletions(nameLowCase, allModules)
    }
}