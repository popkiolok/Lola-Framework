package com.lola.framework.core.decoration

import com.lola.framework.core.LClass
import com.lola.framework.core.LFunction

/**
 * Makes decoration listening for adding member functions to class (excluding constructors).
 * When [AddFunctionListener] decoration applied to class,
 * [onFunctionAdded] will be called for every existing function in it.
 */
interface AddFunctionListener<T : Any> : Decoration<LClass<T>> {
    /**
     * This function is called when a function is added to the class.
     *
     * @param function The function that was added.
     */
    fun onFunctionAdded(function: LFunction<*>)
}