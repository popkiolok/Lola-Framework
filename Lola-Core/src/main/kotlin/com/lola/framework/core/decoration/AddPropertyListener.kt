package com.lola.framework.core.decoration

import com.lola.framework.core.LClass
import com.lola.framework.core.LProperty

/**
 * Makes decoration listening for adding properties to class.
 * When [AddPropertyListener] decoration applied to class,
 * [onPropertyAdded] will be called for every existing property in it.
 */
interface AddPropertyListener<T : Any> : Decoration<LClass<T>> {
    /**
     * This function is called when a property is added to the container.
     *
     * @param property The property that was added.
     */
    fun onPropertyAdded(property: LProperty<*>)
}