package com.lola.framework.core.kotlin

import com.lola.framework.core.container.AbstractContainer
import com.lola.framework.core.kotlin.parametersresolving.AnnotationPropertyParametersResolver
import com.lola.framework.core.kotlin.parametersresolving.NamePropertyParametersResolver
import com.lola.framework.core.kotlin.parametersresolving.PropertyParametersResolver
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmName

class KotlinContainer(
    override val clazz: KClass<*>,
    ppResolvers: Iterable<PropertyParametersResolver> = listOf(
        NamePropertyParametersResolver,
        AnnotationPropertyParametersResolver
    )
) : AbstractContainer() {
    override val name = clazz.simpleName ?: clazz.jvmName
    override val isFinal = clazz.isFinal
    override val annotations = KotlinAnnotationResolver(clazz)

    init {
        val constructors = clazz.constructors.map { KotlinConstructor(it) }
        addConstructors(constructors)
        val allParams = constructors.flatMap { it.parameters }

        val declaredMemberProperties = clazz.declaredMemberProperties
        val declaredMemberFunctions = clazz.declaredMemberFunctions

        val props = declaredMemberProperties.map { prop ->
            KotlinProperty(prop, ppResolvers.asSequence().flatMap { it.resolve(allParams, prop) }.toSet().toList())
        }
        addProperties(props)

        val funElements = declaredMemberFunctions.map { KotlinFunction(it) }
        addFunctions(funElements)

        val superClassSet = clazz.superclasses.toSet()
        superContainers += clazz.allSuperclasses.map {
            val superContainer = getKotlinContainer(it) ?: KotlinContainer(it, ppResolvers)
            if (superClassSet.contains(it)) {
                superContainer.implementations += this
            }
            superContainer
        }

        kotlinContainersByKClass[clazz] = this
        afterInit()
    }
}