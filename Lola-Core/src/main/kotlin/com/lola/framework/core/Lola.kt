package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.DefaultDecorator
import com.lola.framework.core.decoration.ResolveClassListener
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import java.util.ArrayList
import kotlin.reflect.KClass

object Lola : Decorated() {
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
        decorate(DefaultDecorator(this))
    }

    fun <T : Decoration<*>> hasDecoratedClasses(decoration: KClass<T>): Boolean {
        return classes.values.any { it.hasDecoration(decoration) }
    }

    fun <T : Decoration<*>> getDecoratedClasses(decoration: KClass<T>): Sequence<T> {
        return classes.values.asSequence().mapNotNull { it.getDecorations(decoration).firstOrNull() }
    }

    inline fun <reified T : Decoration<*>> hasDecoratedClasses(): Boolean = hasDecoratedClasses(T::class)

    inline fun <reified T : Decoration<*>> getDecoratedClasses(): Sequence<T> = getDecoratedClasses(T::class)

    override fun <T : Decorated> decorate(decoration: Decoration<T>) {
        super.decorate(decoration)
        if (decoration is ResolveClassListener) {
            classes.values.forEach { decoration.onClassFound(it) }
        }
    }
}