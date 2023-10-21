package com.lola.framework.core.property.decorations

import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.property.PropertyDecoration

/**
 * Interface for listening to property get events.
 */
interface PropertyGetListener : PropertyDecoration {

    /**
     * Callback function called when a property is accessed.
     *
     * @param instance The instance of the container.
     * @param value The current value of the property.
     * @return The value of the property to return.
     */
    fun onPropertyGet(instance: ContainerInstance, value: Any?): Any?
}