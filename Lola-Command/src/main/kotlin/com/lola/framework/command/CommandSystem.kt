package com.lola.framework.command

import com.lola.framework.core.LParameter
import com.lola.framework.core.context.Auto
import com.lola.framework.core.context.Context
import com.lola.framework.module.Module
import com.lola.framework.setting.Setting
import org.apache.commons.text.similarity.LevenshteinDistance
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

@Module("Lola-Command:Command System")
class CommandSystem(
    @Setting("Info Output")
    var info: (String) -> Unit = { log.info { it } },

    @Setting("Tooltip Output")
    var tooltip: (String) -> Unit = { log.info { it } },

    @Setting("Warn Output")
    var warn: (String) -> Unit = { log.warn { it } },

    @Setting("Error Output")
    var error: (String) -> Unit = { log.error { it } },

    @Auto
    private val context: Context,
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
                    val (params, props) = (parsedArgs.value as Map<ArgumentReference, ParseResultSuccess<*>>).entries.partition { it.key.target is LParameter }
                    val propValues = props.associate { it.key.target.self as KProperty<*> to it.value.value }
                    val paramValues = params.associate { it.key.target.self as KParameter to it.value.value }
                    log.trace { "Running command $cmd." }
                    val inst = cmd.target.createInstance(paramValues, propValues, ctxInitializer = { ctx ->
                        ctx.parents += context
                    }) as Runnable
                    inst.run()
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

    private fun extractArgs(command: String, cmd: CommandClass) =
        if (command.length == cmd.data.name.length) "" else command.substring(cmd.data.name.length + 1)

    private fun getCommand(command: String) = commands.find {
        command.startsWith(it.data.name) && (command.length == it.data.name.length || command[it.data.name.length] == ' ')
    }

    fun getCompletions(commandPart: String): List<Completion> {
        val cmdLowerCase = commandPart.lowercase()
        val commandNames = commands.asSequence()
            .map { it.data.name.lowercase() }
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

    private fun getCompletionsFor(args: String, cmd: CommandClass, offset: Int): List<Completion> {
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