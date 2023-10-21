package com.lola.framework.command.jmh

import com.lola.framework.command.Command
import com.lola.framework.command.CommandSystem
import com.lola.framework.command.arguments.ArgumentBigDecimal
import com.lola.framework.core.kotlin.KotlinContainer
import com.lola.framework.core.kotlin.LolaKotlin
import com.lola.framework.setting.Setting
import org.openjdk.jmh.annotations.*
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 1)
@Warmup(iterations = 2, timeUnit = TimeUnit.MILLISECONDS, time = 10000)
@Measurement(iterations = 2, timeUnit = TimeUnit.MILLISECONDS, time = 10000)
open class CommandExecutionBenchmark {
    @Benchmark
    fun execute3ArgsCommandBenchmark(env: Env) {
        env.cs.onCommand("test 4568.15 47.5 874.1524")
    }

    @State(Scope.Benchmark)
    open class Env {
        val cs = CommandSystem()

        init {
            KotlinContainer(TestCommand::class)
            KotlinContainer(ArgumentBigDecimal::class)
        }

        @Command("test")
        class TestCommand : Runnable {
            @Setting("X")
            lateinit var x: BigDecimal

            @Setting("Y")
            var y: BigDecimal = BigDecimal(0)

            @Setting("Z")
            lateinit var z: BigDecimal

            override fun run() {}
        }
    }
}