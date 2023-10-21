package com.lola.framework.core.container.navigation

import com.lola.framework.core.container.Container

/**
 * Find [Container]s in [ContainerCluster] by some value, unique for every container in cluster.
 *
 * @param T Type of value to navigate containers by.
 */
interface ContainerNavigator<T : Any> {
    val containers: Collection<Container>

    /**
     * Gets [Container] instance from [containers] by [key].
     *
     * @throws NoSuchElementException If such [Container] does not
     *     exist.
     */
    operator fun get(key: T): Container

    /**
     * Gets if [key] is correct key for [value].
     */
    fun isPair(key: T, value: Container): Boolean
}