package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.*
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import java.io.PrintStream
import java.util.ArrayList
import kotlin.reflect.*

object Lola : Decorated(), DecorateListener<Lola>, DecorateClassListener<Lola>, DecorateConstructorListener<Lola>,
    DecorateMemberListener<Lola>, DecorateParameterListener<Lola> {
    val context = Context(ArrayList())

    fun initialize(vararg packages: Package) {
        println(
            """
            
            ██╗░░░░░░█████╗░██╗░░░░░░█████╗░  ███████╗██████╗░░█████╗░███╗░░░███╗███████╗░██╗░░░░░░░██╗░█████╗░██████╗░██╗░░██╗
            ██║░░░░░██╔══██╗██║░░░░░██╔══██╗  ██╔════╝██╔══██╗██╔══██╗████╗░████║██╔════╝░██║░░██╗░░██║██╔══██╗██╔══██╗██║░██╔╝
            ██║░░░░░██║░░██║██║░░░░░███████║  █████╗░░██████╔╝███████║██╔████╔██║█████╗░░░╚██╗████╗██╔╝██║░░██║██████╔╝█████═╝░
            ██║░░░░░██║░░██║██║░░░░░██╔══██║  ██╔══╝░░██╔══██╗██╔══██║██║╚██╔╝██║██╔══╝░░░░████╔═████║░██║░░██║██╔══██╗██╔═██╗░
            ███████╗╚█████╔╝███████╗██║░░██║  ██║░░░░░██║░░██║██║░░██║██║░╚═╝░██║███████╗░░╚██╔╝░╚██╔╝░╚█████╔╝██║░░██║██║░╚██╗
            ╚══════╝░╚════╝░╚══════╝╚═╝░░╚═╝  ╚═╝░░░░░╚═╝░░╚═╝╚═╝░░╚═╝╚═╝░░░░░╚═╝╚══════╝░░░╚═╝░░░╚═╝░░░╚════╝░╚═╝░░╚═╝╚═╝░░╚═╝
        """.trimIndent()
        )
        val reflections = Reflections(
            ConfigurationBuilder().forPackages(
                "com.lola.framework",
                *Array(packages.size) { i -> packages[i].name }).setScanners(Scanners.SubTypes.filterResultsBy { true })
        )
        reflections.getSubTypesOf(Any::class.java).forEach {
            runCatching { it.kotlin.lola }.onFailure { it.printStackTrace() }
        }
        ForAnnotatedDecorator::class.lola.let {
            it.decorate(ForAnnotatedDecorator(it, ForAnnotated(ForAnnotated::class)))
        }
    }

    fun printInfo(stream: PrintStream = System.out) {
        val printFor = { name: String, vs: Map<*, Decorated> ->
            val d = vs.values.count { it: Decorated -> it.hasDecoration<Decoration<*>>() }
            val nd = vs.values.sumOf { it: Decorated -> it.getDecorations<Decoration<*>>().size }
            stream.println("$name: ${vs.size} total, $d decorated, $nd decorations.")
        }
        stream.println("-------------------- Lola Framework Info --------------------")
        printFor("Classes", classMap)
        printFor("Callables", callables)
        printFor("Parameters", parameters)
        stream.println("Objects with context: ${instanceToContext.size} total.")
        stream.println("-------------------------------------------------------------")
    }

    fun <T : Decoration<*>> hasDecoratedClasses(decoration: KClass<T>): Boolean {
        return classMap.values.any { it.hasDecoration(decoration) }
    }

    fun <T : Decoration<*>> getDecoratedClasses(decoration: KClass<T>): Sequence<T> {
        return classMap.values.asSequence().mapNotNull { it.getDecorations(decoration).firstOrNull() }
    }

    inline fun <reified T : Decoration<*>> hasDecoratedClasses(): Boolean = hasDecoratedClasses(T::class)

    inline fun <reified T : Decoration<*>> getDecoratedClasses(): Sequence<T> = getDecoratedClasses(T::class)

    override fun <T : Decorated> decorate(decoration: Decoration<T>) {
        super.decorate(decoration)
        onDecorated(decoration as Decoration<Lola>)
    }

    override val target: Lola
        get() = this

    override fun onDecorated(decoration: Decoration<Lola>) {
        classMap.values.forEach { (it as LClass<Any>).onDecorated(decoration as Decoration<LClass<Any>>) }
        if (decoration is ResolveClassListener<Lola>) {
            classMap.values.forEach { decoration.onClassFound(it) }
        }
        if (decoration is ResolveLolaListener<Lola>) {
            decoration.onLolaFound(this)
        }
    }

    override fun <T : Any> onDecoratedClass(decoration: Decoration<LClass<T>>) {
        getDecorations<DecorateClassListener<*>>().forEach { it.onDecoratedClass(decoration) }
    }

    override fun <T : Any> onDecoratedConstructor(decoration: Decoration<LCallable<T, KFunction<T>>>) {
        getDecorations<DecorateConstructorListener<*>>().forEach { it.onDecoratedConstructor(decoration) }
    }

    override fun <T> onDecoratedMember(decoration: Decoration<LCallable<T, KCallable<T>>>) {
        getDecorations<DecorateMemberListener<*>>().forEach { it.onDecoratedMember(decoration) }
    }

    override fun onDecoratedParameter(decoration: Decoration<LParameter>) {
        getDecorations<DecorateParameterListener<*>>().forEach { it.onDecoratedParameter(decoration) }
    }
}