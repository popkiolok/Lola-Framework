package com.lola.framework.core.property.decorations

import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.property.PropertyDecoration
import com.lola.framework.core.util.Option

/**
 * Interface for listening to property set events.
 */
interface PropertySetListener : PropertyDecoration {
    /**
     * Callback function called when a property is set.
     *
     * @param instance The instance of the container.
     * @param value The value to be set for the property.
     * @return Optional value to set, empty if value should not be set.
     */
    fun onPropertySet(instance: ContainerInstance, value: Any?): Option<Any?>
}