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

object Lola : Decorated(), ResolveElementAnywhereListener<Lola>, ResolveClassAnywhereListener<Lola>,
    ResolveConstructorAnywhereListener<Lola>, ResolveParameterAnywhereListener<Lola>,
    ResolveCallableAnywhereListener<Lola>, ResolvePropertyAnywhereListener<Lola>, ResolveFunctionAnywhereListener<Lola>,
    ResolveStaticCallableAnywhereListener<Lola>, ResolveStaticPropertyAnywhereListener<Lola>,
    ResolveStaticFunctionAnywhereListener<Lola>, ResolveMemberCallableAnywhereListener<Lola>,
    ResolveMemberPropertyAnywhereListener<Lola>, ResolveMemberFunctionAnywhereListener<Lola>, DecorateListener<Lola>,
    DecorateClassListener<Lola>, DecorateClassConstructorListener<Lola>, DecorateClassMemberListener<Lola> {
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
        onDecorated(decoration)
    }

    override val target: Lola
        get() = this

    override fun onElementFoundAnywhere(element: LAnnotatedElement) {
        TODO("Not yet implemented")
    }

    override fun <T : Any> onClassFoundAnywhere(clazz: LClass<T>) {
        TODO("Not yet implemented")
    }

    override fun <T : Any> onConstructorFoundAnywhere(constructor: LCallable<T, KFunction<T>>) {
        TODO("Not yet implemented")
    }

    override fun <T : Any> onParameterFoundAnywhere(constructor: LCallable<T, KFunction<T>>) {
        TODO("Not yet implemented")
    }

    override fun <R> onCallableFoundAnywhere(member: LCallable<R, KCallable<R>>) {
        TODO("Not yet implemented")
    }

    override fun <R> onPropertyFoundAnywhere(property: LCallable<R, KProperty<R>>) {
        TODO("Not yet implemented")
    }

    override fun <R> onFunctionFoundAnywhere(function: LCallable<R, KFunction<R>>) {
        TODO("Not yet implemented")
    }

    override fun <R> onStaticCallableFoundAnywhere(member: LCallable<R, KCallable<R>>) {
        TODO("Not yet implemented")
    }

    override fun <R> onStaticPropertyFoundAnywhere(property: LCallable<R, KProperty<R>>) {
        TODO("Not yet implemented")
    }

    override fun <R> onStaticFunctionFoundAnywhere(function: LCallable<R, KFunction<R>>) {
        TODO("Not yet implemented")
    }

    override fun <R> onMemberCallableFoundAnywhere(member: LCallable<R, KCallable<R>>) {
        TODO("Not yet implemented")
    }

    override fun <T : Any, R> onMemberPropertyFoundAnywhere(property: LCallable<R, KProperty1<T, R>>) {
        TODO("Not yet implemented")
    }

    override fun <R> onMemberFunctionFoundAnywhere(function: LCallable<R, KFunction<R>>) {
        TODO("Not yet implemented")
    }

    override fun onDecorated(decoration: Decoration<Lola>) {
        if (decoration is ResolveClassAnywhereListener) {
            classes.values.forEach { decoration.onClassFoundAnywhere(it) }
        }
        if (decoration is ResolveMemberCallableAnywhereListener)
            classes.values.forEach { clazz ->
                clazz.self.members.forEach { decoration.onMemberCallableFoundAnywhere(clazz, it.lola) }
            }
        if (decoration is ResolveMemberFunctionAnywhereListener)
            classes.values.forEach { clazz ->
                clazz.self.memberFunctions.forEach { decoration.onMemberFunctionFoundAnywhere(clazz, it.lola) }
            }
        if (decoration is ResolveMemberPropertyAnywhereListener)
            classes.values.forEach { clazz -> onClassPropertyFound(clazz, decoration) }
        if (decoration is ResolveConstructorAnywhereListener)
            classes.values.forEach { clazz -> onClassConstructorFound(clazz, decoration) }
    }

    override fun <T : Any> onDecoratedClass(decoration: Decoration<LClass<T>>) {
        if (decoration is ResolveConstructorInClassListener<T>) {
            self.constructors.forEach { decoration.onConstructorFoundInClass(it.lola) }
        }
        if (decoration is ResolveMemberCallableInClassListener<T>) {
            self.members.forEach { decoration.onMemberCallableFoundInClass(it.lola) }
        }
        if (decoration is ResolveMemberPropertyInClassListener<T>) {
            self.memberProperties.forEach { decoration.onMemberPropertyFoundInClass(it.lola) }
        }
        if (decoration is ResolveMemberFunctionInClassListener<T>) {
            self.memberFunctions.forEach { decoration.onMemberFunctionFoundInClass(it.lola) }
        }
        if (decoration is DecorateConstructorListener<T>) {
            getDecoratedConstructors<Decoration<LCallable<T, KFunction<T>>>>().forEach { decoration.onDecoratedConstructor(it) }
        }
        if (decoration is DecorateMemberListener<T>) {
            getDecoratedMembers<Decoration<LCallable<T, KCallable<T>>>>().forEach { decoration.onDecoratedMember(it) }
        }
    }

    override fun <T : Any> onDecoratedClassConstructor(decoration: Decoration<LCallable<T, KFunction<T>>>) {
        TODO("Not yet implemented")
    }

    override fun <T> onDecoratedClassMember(decoration: Decoration<LCallable<T, KCallable<T>>>) {
        TODO("Not yet implemented")
    }

    /*private fun <T : Any> onClassPropertyFound(clazz: LClass<T>, decoration: ResolveMemberPropertyAnywhereListener) {
        clazz.self.memberProperties.forEach { decoration.onMemberPropertyFoundAnywhere(clazz, it.lola) }
    }

    private fun <T : Any> onClassConstructorFound(clazz: LClass<T>, decoration: ResolveConstructorAnywhereListener) {
        clazz.self.constructors.forEach { decoration.onConstructorFoundAnywhere(clazz, it.lola) }
    }

    override fun <T : Any> onDecoratedClass(decoration: Decoration<LClass<T>>) {
        TODO("Not yet implemented")
    }

    override fun <T : Any> onDecoratedClassConstructor(decoration: Decoration<LCallable<T, KFunction<T>>>) {
        getDecorations<DecorateClassConstructorListener<*>>().forEach {
            it.onDecoratedClassConstructor(decoration)
        }
    }

    override fun <T> onDecoratedClassMember(decoration: Decoration<LCallable<T, KCallable<T>>>) {
        getDecorations<DecorateClassMemberListener<*>>().forEach {
            it.onDecoratedClassMember(decoration)
        }
    }*/
}