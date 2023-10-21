package com.lola.framework.command.example

import com.lola.framework.command.Command
import com.lola.framework.command.CommandSystem
import com.lola.framework.core.kotlin.LolaKotlin
import com.lola.framework.setting.Setting
import java.math.BigDecimal
import java.math.MathContext

fun main() {
    LolaKotlin()
    val cs = CommandSystem()
    cs.onCommand("length 6 8")
    cs.onCommand("length 78 56 78")
}

@Command("length")
class LengthOfVector : Runnable {
    @Setting("X")
    lateinit var x: BigDecimal

    @Setting("Y")
    var y: BigDecimal = BigDecimal(0)

    @Setting("Z")
    lateinit var z: BigDecimal

    override fun run() {
        println("X is $x, Y is $y, Z is $z")
        println("Length is ${(x * x + y * y + z * z).sqrt(ctx)}.")
    }

    companion object {
        private val ctx = MathContext(8)
    }
}