package com.lola.framework.core

import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.*
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import java.util.ArrayList
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

object Lola : Decorated(), DecorateClassConstructorListener, DecorateClassMemberListener {
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
        // Not 'when' because decoration can implement multiple interfaces
        if (decoration is ResolveClassListener) {
            classes.values.forEach { decoration.onClassFound(it) }
        }
        if (decoration is ResolveClassMemberListener)
            classes.values.forEach { clazz ->
                clazz.kClass.members.forEach { decoration.onClassMemberFound(clazz, it.lola) }
            }
        if (decoration is ResolveClassMemberFunctionListener)
            classes.values.forEach { clazz ->
                clazz.kClass.memberFunctions.forEach { decoration.onClassFunctionFound(clazz, it.lola) }
            }
        if (decoration is ResolveClassMemberPropertyListener)
            classes.values.forEach { clazz -> onClassPropertyFound(clazz, decoration) }
        if (decoration is ResolveClassConstructorListener)
            classes.values.forEach { clazz -> onClassConstructorFound(clazz, decoration) }
    }

    private fun <T : Any> onClassPropertyFound(clazz: LClass<T>, decoration: ResolveClassMemberPropertyListener) {
        clazz.kClass.memberProperties.forEach { decoration.onClassPropertyFound(clazz, it.lola) }
    }

    private fun <T : Any> onClassConstructorFound(clazz: LClass<T>, decoration: ResolveClassConstructorListener) {
        clazz.kClass.constructors.forEach { decoration.onClassConstructorFound(clazz, it.lola) }
    }

    override fun <T : Any> onDecoratedClassConstructor(
        clazz: LClass<T>,
        constructor: LCallable<T, KFunction<T>>,
        decoration: Decoration<LCallable<T, KFunction<T>>>
    ) {
        getDecorations<DecorateClassConstructorListener>().forEach {
            it.onDecoratedClassConstructor(clazz, constructor, decoration)
        }
    }

    override fun <T : Any> onDecoratedClassMember(
        clazz: LClass<T>,
        member: LCallable<*, KCallable<*>>,
        decoration: Decoration<LCallable<*, KCallable<*>>>
    ) {
        getDecorations<DecorateClassMemberListener>().forEach {
            it.onDecoratedClassMember(clazz, member, decoration)
        }
    }

    override val target: Lola
        get() = this
}