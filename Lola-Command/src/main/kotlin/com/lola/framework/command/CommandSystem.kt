package com.lola.framework.command

import com.lola.framework.core.container.context.Auto
import com.lola.framework.core.container.context.Context
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.property.Property
import com.lola.framework.module.Module
import com.lola.framework.setting.Setting

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

    fun onCommand(command: String) {
        log.debug { "Executing command $command." }
        CommandRegistry.commands.find {
            command.startsWith(it.name) && (command.length == it.name.length || command[it.name.length] == ' ')
        }?.let { cmd ->
            val args = if (command.length == cmd.name.length) "" else command.substring(cmd.name.length + 1)
            log.trace { "Parsing arguments $args." }
            val res = parseArgsAndRun(args, cmd, cmd.arguments)
            if (res != null) {
                error("Unable to parse command arguments: ")
                res.messages.forEach { error(it()) }
            }
        }
    }

    private fun parseArgsAndRun(
        args: String,
        cmd: CommandContainer,
        argsList: List<ArgumentProperty>
    ): ParseResultMultiError? {
        val propValues = HashMap<Property, Any?>()
        val paramValues = HashMap<Parameter, Any?>()
        var argsLeft = args
        val last = argsList.lastIndex
        argsList.forEachIndexed { argIndex, arg ->
            val parseResult = arg.parse(argsLeft, argIndex == last) // TODO: faster with custom for excluding last element instead if index check
            if (parseResult is ParseResultSuccess) {
                argsLeft = argsLeft.substring(parseResult.argLength)
                val value = parseResult.value
                log.trace { "Parsed argument $value." }
                propValues[arg.self] = value
                arg.self.parameters.forEach { paramValues[it] = value }
            } else if (parseResult is ParseResultMultiError) {
                val lastNotReq = argsList.indexOfLast { !it.required }
                if (lastNotReq == -1) {
                    return parseResult
                }
                val newArgsList = ArrayList<ArgumentProperty>()
                for (i in 0..<lastNotReq)
                    newArgsList += argsList[i]
                for (i in (lastNotReq + 1)..<argsList.size)
                    newArgsList += argsList[i]
                return parseArgsAndRun(args, cmd, newArgsList)
            }
        }

        log.trace { "Running command $cmd." }
        val ci = cmd.self.createInstance(paramValues, propValues, ctxInitializer = { ctx ->
            ctx.parents += context
        })
        (ci.instance as Runnable).run()
        return null
    }
}