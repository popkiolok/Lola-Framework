package com.lola.framework.command.arguments

import com.lola.framework.command.*
import com.lola.framework.module.*
import org.apache.commons.text.similarity.LevenshteinDistance
import kotlin.math.min
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ArgumentModuleClass : ArgumentString() {

    override fun canParse(type: KType) = type.jvmErasure == ModuleClass::class

    override fun parse(pctx: ParsingContext): ParseResult {
        val asString = (super.parseAsString(pctx.input, pctx.isLast))
        val name = asString.value
        val module = modulesByName[name] ?: modulesByName.entries.firstOrNull { (n, _) ->
                name.equals(n, ignoreCase = true) || name.equals(n.replace(" ", ""), ignoreCase = true)
            }?.value ?: modulesByName.values.firstOrNull {
                it.data.simpleName.equals(name, ignoreCase = true) ||
                        it.data.simpleName.replace(" ", "").equals(name, ignoreCase = true)
            }
            ?: run {
                val allModules = modulesByName.entries.flatMap { (n, m) ->
                    listOf(n.lowercase(), m.data.simpleName.lowercase())
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
        return modulesByName.entries
            .sortedBy { (n, m) ->
                val nLowCase = n.lowercase()
                min(
                    LevenshteinDistance.getDefaultInstance().apply(nameLowCase, nLowCase),
                    LevenshteinDistance.getDefaultInstance().apply(nameLowCase, m.data.simpleName.lowercase())
                ).let { if (nLowCase.contains(nameLowCase)) it else it * 4 }
            }.map { it.key }.let { strings ->
                if (isLast) strings else strings.map { "\"$it\"" }
            }
    }
}