package com.lola.framework.core.decoration

import com.lola.framework.core.LClass
import com.lola.framework.core.LFunction

/**
 * Makes decoration listening for adding constructors to class.
 * When [AddConstructorListener] decoration applied to class,
 * [onConstructorAdded] will be called for every existing function in it.
 */
interface AddConstructorListener<T : Any> : Decoration<LClass<T>> {
    /**
     * This function is called when a constructor is added to the class.
     *
     * @param constructor The constructor that was added.
     */
    fun onConstructorAdded(constructor: LFunction<T>)
}