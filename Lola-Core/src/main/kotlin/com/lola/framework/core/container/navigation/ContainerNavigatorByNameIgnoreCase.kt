package com.lola.framework.core.container.navigation

import com.lola.framework.core.container.Container

class ContainerNavigatorByNameIgnoreCase(containers: Collection<Container>) :
    ContainerNavigatorByName(containers) {
    override operator fun get(key: String): Container {
        val lowCase = key.lowercase()
        return cache.computeIfAbsent(lowCase) {
            containers.find { isPair0(lowCase, it) } ?: throw NoSuchElementException()
        }
    }

    override fun isPair(key: String, value: Container): Boolean {
        return value.name.lowercase() == key.lowercase()
    }

    // Faster isPair that does not lowercase() key every call
    private fun isPair0(key: String, value: Container): Boolean {
        return value.name.lowercase() == key
    }
}