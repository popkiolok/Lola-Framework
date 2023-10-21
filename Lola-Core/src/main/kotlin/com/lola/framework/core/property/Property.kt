package com.lola.framework.core.property

import com.lola.framework.core.Element
import com.lola.framework.core.Nameable
import com.lola.framework.core.Type
import com.lola.framework.core.annotation.Annotated
import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.property.decorations.PropertyValueSupplier
import com.lola.framework.core.util.Option

/**
 * Represents an element of a container that can keep some value.
 */
interface Property : Element, Nameable, Decorated<PropertyDecoration>,
    Annotated {
    /**
     * Returns the type of the property.
     */
    val type: Type

    /**
     * Returns if property is mutable or not.
     */
    val mutable: Boolean

    /**
     * Returns true if property is immutable, false otherwise.
     */
    val immutable: Boolean
        get() = !mutable

    /**
     * Constructors parameters associated with this property
     * (parameter, that is used to set initial value of property).
     */
    val parameters: Collection<Parameter>

    /**
     * Returns if property has default value.
     */
    val hasDefaultValue: Boolean

    /**
     * Returns [Option] with default value of property, if possible to retrieve, empty [Option] otherwise.
     */
    val defaultValue: Option<Any?>

    /**
     * Returns iterable of initializers for this property.
     */
    val initializers: Iterable<PropertyValueSupplier>

    /**
     * Retrieves the value of the property for the given container instance.
     *
     * @param instance the container instance.
     * @return the value of the property.
     */
    operator fun get(instance: ContainerInstance): Any?

    /**
     * Sets the value of the property for the given container instance.
     *
     * @param instance the container instance.
     * @param value the value to set.
     * @throws IllegalStateException if the property is immutable.
     */
    operator fun set(instance: ContainerInstance, value: Any?)

    fun undecoratedCopy(parameters: List<Parameter>): Property
}