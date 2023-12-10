package com.lola.framework.command

/**
 * Annotate class as [CommandClass].
 *
 * @param name Command name. Can contain spaces for subcommands.
 */
annotation class Command(val name: String, val info: String = "")
