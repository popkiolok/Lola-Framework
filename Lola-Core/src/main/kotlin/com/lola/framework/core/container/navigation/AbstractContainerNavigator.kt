package com.lola.framework.core.container.navigation

import com.lola.framework.core.container.Container

abstract class AbstractContainerNavigator<T : Any>(
    override val containers: Collection<Container>
) : ContainerNavigator<T> {

    protected val cache: MutableMap<T, Container> = HashMap()

    override operator fun get(key: T): Container {
        return cache.computeIfAbsent(key) {
            containers.find { isPair(key, it) } ?: throw NoSuchElementException()
        }
    }
}