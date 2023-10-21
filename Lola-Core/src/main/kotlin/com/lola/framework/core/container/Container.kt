package com.lola.framework.core.container

import com.lola.framework.core.Element
import com.lola.framework.core.Nameable
import com.lola.framework.core.annotation.Annotated
import com.lola.framework.core.constructor.Constructor
import com.lola.framework.core.container.context.Context
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.function.Function
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.property.Property
import kotlin.reflect.KClass

/**
 * Represents nameable, decorated and marked structure that can contain other elements.
 */
interface Container : Nameable, Decorated<ContainerDecoration>, Annotated {
    val clazz: KClass<*>

    /**
     * All found super containers of this container.
     */
    val superContainers: List<Container>

    /**
     * Indicates whether this container is final and can't have child containers.
     */
    val isFinal: Boolean

    /**
     * All found implementations of this container, including only direct child containers.
     */
    val implementations: Collection<Container>

    /**
     * Returns properties in the container, including only properties defined directly
     * in this container, not in super containers.
     */
    val properties: Collection<Property>

    /**
     * Returns all properties in this container and its super containers.
     */
    val allProperties: Iterable<Property>

    /**
     * Returns constructors of the container.
     */
    val constructors: Collection<Constructor>

    /**
     * Retrieves functions from the container, including only functions defined directly
     * in this container, not in super containers.
     *
     * @return A collection of functions.
     */
    val functions: Collection<Function>

    /**
     * Retrieves all functions from this container and its super containers.
     *
     * @return An iterable of functions.
     */
    val allFunctions: Iterable<Function>

    /**
     * Container class level context. Extends [globalContext].
     */
    val context: Context

    /**
     * Creates new [ContainerInstance] for this [Container] using first [Constructor],
     * which parameters can be completed with parameters from [params].
     *
     * @param params Map of parameters invoker can provide.
     * @param propertyValues Initial values of new container properties.
     * @return New [ContainerInstance].
     * @throws NullPointerException If no constructor present for the given parameters.
     */
    fun createInstance(
        params: Map<Parameter, Any?>,
        propertyValues: Map<Property, Any?> = emptyMap(),
        ctxInitializer: (Context) -> Unit = {}
    ): ContainerInstance

    /**
     * Adds an element to the container, retrieving it type automatically.
     *
     * @param element The element to be added.
     */
    fun addElement(element: Element)

    /**
     * Adds a property to the container.
     *
     * @param property The property to be added.
     */
    fun addProperty(property: Property)

    /**
     * Adds properties to the container.
     *
     * @param properties Iterable of property to be added.
     */
    fun addProperties(properties: Iterable<Property>)

    /**
     * Adds a constructor to the container.
     *
     * @param constructor The constructor to be added.
     */
    fun addConstructor(constructor: Constructor)

    /**
     * Adds constructors to the container.
     *
     * @param constructors Iterable of constructors to be added.
     */
    fun addConstructors(constructors: Iterable<Constructor>)

    /**
     * Adds a function to the container.
     *
     * @param function Function to be added.
     */
    fun addFunction(function: Function)

    /**
     * Adds functions to the container.
     *
     * @param functions Iterable of functions to be added.
     */
    fun addFunctions(functions: Iterable<Function>)

    fun hasDefaultConstructor(): Boolean
}