package com.lola.framework.core.container

/**
 * This interface represents a listener for creating containers.
 * When [AddContainerListener] decoration subscribed with [subscribeAddContainerListener],
 * [onContainerAdded] will be called for every already existing container.
 */
interface AddContainerListener {
    /**
     * This function is called when a new [Container] class is created.
     *
     * @param container The container that was created.
     */
    fun onContainerAdded(container: Container)
}