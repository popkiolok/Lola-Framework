package com.lola.framework.core.decoration

import com.lola.framework.core.LClass

/**
 * Makes implementation listening for finding new [LClass]es (Creating [LClass] instances).
 * When [FoundClassListener] subscribed, [onClassFound] will be called for every already found class.
 */
interface FoundClassListener {
    /**
     * This function is called when new class is found ([LClass] object is created).
     *
     * @param clazz The class that was found.
     */
    fun onClassFound(clazz: LClass<*>)
}