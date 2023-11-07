package com.lola.framework.command

import com.lola.framework.core.container.context.Auto
import com.lola.framework.core.container.context.Context
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.property.Property
import com.lola.framework.module.Module
import com.lola.framework.setting.Setting
import com.lola.framework.setting.setting
import org.apache.commons.text.similarity.LevenshteinDistance

@Module(group = "Lola-Command", path = "Command System")
class CommandSystem(
    @Auto private val context: Context,

    @Setting("Info Output")
    var info: (String) -> Unit = { log.info { it } },

    @Setting("Tooltip Output")
    var tooltip: (String) -> Unit = { log.info { it } },

    @Setting("Warn Output")
    var warn: (String) -> Unit = { log.warn { it } },

    @Setting("Error Output")
    var error: (String) -> Unit = { log.error { it } }
) {

    @Suppress("UNCHECKED_CAST")
    fun onCommand(command: String) {
        log.debug { "Executing command $command." }
        runCatching {
            getCommand(command)?.also { cmd ->
                val args = extractArgs(command, cmd)
                log.trace { "Parsing arguments $args." }
                val parsedArgs = cmd.orderedArgs.parseAsMap(args, context)
                if (parsedArgs is ParseResultSuccess<*>) {
                    val map = parsedArgs.value as Map<ArgumentProperty, ParseResultSuccess<*>>
                    val propValues = map.mapKeys { it.key.self }.mapValues { it.value.value }
                    val paramValues =
                        propValues.flatMap { (prop, value) -> prop.parameters.map { it to value } }.toMap()
                    log.trace { "Running command $cmd." }
                    val ci = cmd.self.createInstance(paramValues, propValues, ctxInitializer = { ctx ->
                        ctx.parents += context
                    })
                    (ci.instance as Runnable).run()
                } else if (parsedArgs is ParseResultMultiError) {
                    error("Unable to parse command arguments: ")
                    parsedArgs.messages.forEach { error(it()) }
                } else if (parsedArgs is ParseResultFailure) {
                    error("Unable to parse command arguments: ")
                    error(parsedArgs.message())
                }
            } ?: error("Command '$command' does not exist.")
        }.onFailure {
            log.error { "Command '$command' execution finished with error '${it.message}'." }
            it.printStackTrace()
        }
    }

    private fun extractArgs(command: String, cmd: CommandContainer) =
        if (command.length == cmd.name.length) "" else command.substring(cmd.name.length + 1)

    private fun getCommand(command: String) = CommandRegistry.commands.find {
        command.startsWith(it.name) && (command.length == it.name.length || command[it.name.length] == ' ')
    }

    fun getCompletions(commandPart: String): List<Completion> {
        val cmdLowerCase = commandPart.lowercase()
        val commandNames = CommandRegistry.commands.asSequence()
            .map { it.name.lowercase() }
            .filter { it.startsWith(cmdLowerCase) }
            .sortedBy { LevenshteinDistance.getDefaultInstance().apply(cmdLowerCase, it) }.toList()
        if (commandNames.isEmpty()) {
            val cmd = getCommand(commandPart) ?: return emptyList()
            val args = extractArgs(commandPart, cmd)
            return getCompletionsFor(args, cmd, commandPart.length - args.length)
        } else {
            return commandNames.map { Completion(0, it) }
        }
    }

    private fun getCompletionsFor(args: String, cmd: CommandContainer, offset: Int): List<Completion> {
        val parseResult = cmd.orderedArgs.parseAsMap(args, context)
        return if (parseResult is ParseResultMultiError) {
            parseResult.lastFailed.getCompletions(
                args,
                cmd.arguments.indexOf(parseResult.lastFailed) == cmd.arguments.lastIndex,
                parseResult.parsed
            ).map { Completion(offset + parseResult.parsed.values.sumOf { r -> r.argLength }, it) }
        } else {
            emptyList()
        }
    }
}