package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.core.LType
import com.lola.framework.module.ModuleContainer
import com.lola.framework.module.ModuleRegistry
import org.apache.commons.text.similarity.LevenshteinDistance
import kotlin.math.min

class ArgumentModuleContainer : ArgumentString() {

    override fun canParse(type: LType) = type.clazz == ModuleContainer::class

    override fun parse(pctx: ParsingContext): ParseResult {
        val asString = (super.parseAsString(pctx.argsLeft, pctx.isLast))
        val name = asString.value
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
                    return ParseResultFailure { "Module with name '$name' does not exist. Did you mean '$nearest'?" }
                }
                return ParseResultFailure { "Module with name '$name' does not exist." }
            }
        return ParseResultSuccess(module, asString.argLength)
    }

    override fun complete(argsLeft: String, isLast: Boolean): List<String> {
        val asString = super.parseAsString(argsLeft, isLast)
        val name = asString.value
        val nameLowCase = name.lowercase()
        return ModuleRegistry.modulesByName.entries
            .sortedBy { (n, m) ->
                val nLowCase = n.lowercase()
                min(
                    LevenshteinDistance.getDefaultInstance().apply(nameLowCase, nLowCase),
                    LevenshteinDistance.getDefaultInstance().apply(nameLowCase, m.path.toString().lowercase())
                ).let { if (nLowCase.contains(nameLowCase)) it else it * 4 }
            }.map { it.key }.let { strings ->
                if (isLast) strings else strings.map { "\"$it\"" }
            }
    }
}