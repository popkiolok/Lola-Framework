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
        runCatching {
            val constructors = clazz.constructors.map { KotlinConstructor(it) }
            addConstructors(constructors)
            val primaryConstructor = constructors.maxByOrNull { it.parameters.size }
            val allParams = constructors.flatMap { it.parameters }

            // KotlinReflectionInternalError during constructing container for Kotlin Function1 class
            runCatching {
                val declaredMemberProperties = clazz.declaredMemberProperties
                val declaredMemberFunctions = clazz.declaredMemberFunctions

                val props = declaredMemberProperties.map { prop ->
                    KotlinProperty(
                        prop,
                        ppResolvers.asSequence().flatMap { it.resolve(allParams, prop) }.toSet().toList()
                    )
                }
                val orderedProps = props.asSequence().withIndex().sortedBy { (i, p) ->
                    if (p.parameters.isEmpty() || primaryConstructor == null) {
                        65536 + i
                    } else {
                        val paramIndex = p.parameters.maxOf { primaryConstructor.parameters.indexOf(it) }
                        if (paramIndex == -1) 65536 + i else paramIndex
                    }
                }.map { it.value }

                addProperties(orderedProps.toList())

                val funElements = declaredMemberFunctions.map { KotlinFunction(it) }
                addFunctions(funElements)
            }

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
        }.onFailure {
            log.error { "An error occurred while creating container '$this'." }
            it.printStackTrace()
        }
    }

    override fun toString(): String {
        return "[KotlinContainer/${hashCode()}] $name ($clazz)"
    }
}