package com.lola.framework.core.container.decorations

import com.lola.framework.core.container.ContainerDecoration
import com.lola.framework.core.property.Property

/**
 * This interface represents a listener for adding properties to a container.
 * When [AddPropertyListener] added to the container,
 * [onPropertyAdded] will be called for every existing property in it.
 */
interface AddPropertyListener : ContainerDecoration {
    /**
     * This function is called when a property is added to the container.
     *
     * @param property The property that was added.
     */
    fun onPropertyAdded(property: Property)
}