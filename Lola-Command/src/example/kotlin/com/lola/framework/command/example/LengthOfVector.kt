package com.lola.framework.command.example

import com.lola.framework.command.Command
import com.lola.framework.command.CommandSystem
import com.lola.framework.command.Param
import com.lola.framework.core.Lola
import com.lola.framework.core.lola
import com.lola.framework.setting.Setting
import java.math.BigDecimal
import java.math.MathContext

fun main() {
    Lola.initialize()
    val cs = CommandSystem::class.lola.createInstance()
    cs.onCommand("length 6 8")
    cs.onCommand("length 78 56 78")
    Lola.printInfo()
}

@Command("length")
class LengthOfVector : Runnable {
    @Param("X")
    lateinit var x: BigDecimal

    @Param("Y")
    var y: BigDecimal = BigDecimal(0)

    @Param("Z")
    lateinit var z: BigDecimal

    override fun run() {
        println("X is $x, Y is $y, Z is $z")
        println("Length is ${(x * x + y * y + z * z).sqrt(ctx)}.")
    }

    companion object {
        private val ctx = MathContext(8)
    }
}