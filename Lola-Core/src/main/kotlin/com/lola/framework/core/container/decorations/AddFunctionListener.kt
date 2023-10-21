package com.lola.framework.core.container.decorations

import com.lola.framework.core.container.ContainerDecoration
import com.lola.framework.core.function.Function

/**
 * This interface represents a listener for adding functions to a container.
 * When [AddFunctionListener] added to the container,
 * [onFunctionAdded] will be called for every existing function in it.
 */
interface AddFunctionListener : ContainerDecoration {
    /**
     * This function is called when a function is added to the container.
     *
     * @param function The function that was added.
     */
    fun onFunctionAdded(function: Function)
}