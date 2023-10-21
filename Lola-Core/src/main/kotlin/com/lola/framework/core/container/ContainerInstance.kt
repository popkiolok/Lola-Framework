package com.lola.framework.core.container

import com.lola.framework.core.container.context.Context

class ContainerInstance(
    /**
     * The container associated with this instance.
     */
    val container: Container
) {
    /**
     * Container instance context. Extends [Container.context] for container this instance was made for.
     */
    val context = Context(mutableListOf(container.context)).register(
        "ContainerInstance", ContainerInstance::class, ContainerInstance::class.java
    ) { this }

    /**
     * User object - the instance of container itself, handled by this [ContainerInstance].
     *
     * @throws IllegalStateException If [ContainerInstance] is uncompleted and no instance object present.
     */
    val instance: Any
        get() = instance0
            ?: throw IllegalStateException("Accessing instance in uncompleted ContainerInstance not allowed.")

    internal var instance0: Any? = null
}