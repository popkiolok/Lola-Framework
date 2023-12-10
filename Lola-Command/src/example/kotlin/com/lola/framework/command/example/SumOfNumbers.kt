package com.lola.framework.command.example

import com.lola.framework.command.Command
import com.lola.framework.command.CommandSystem
import com.lola.framework.core.Lola
import com.lola.framework.core.lola
import com.lola.framework.setting.Setting
import java.math.BigDecimal

fun main() {
    Lola.initialize()
    CommandSystem::class.lola.createInstance().onCommand("sum 100 78.98")
}

@Command("sum")
class SumOfNumbers(@Setting("A") val a: BigDecimal, @Setting("B") val b: BigDecimal) : Runnable {
    override fun run() {
        println("Sum is ${a.add(b)}.")
    }
}