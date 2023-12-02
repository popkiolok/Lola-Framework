package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.*
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import java.util.ArrayList
import kotlin.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

object Lola : Decorated(), DecorateListener<Lola>, DecorateClassListener<Lola>, DecorateConstructorListener<Lola>,
    DecorateMemberListener<Lola> {
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
        onDecorated(decoration as Decoration<Lola>)
    }

    override val target: Lola
        get() = this

    override fun onDecorated(decoration: Decoration<Lola>) {
        classes.values.forEach { (it as LClass<Any>).onDecorated(decoration as Decoration<LClass<Any>>) }
        if (decoration is ResolveClassListener<Lola>) {
            classes.values.forEach { decoration.onClassFound(it) }
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
}