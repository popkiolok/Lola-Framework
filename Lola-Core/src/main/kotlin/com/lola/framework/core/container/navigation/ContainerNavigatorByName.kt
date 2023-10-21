package com.lola.framework.core.container.navigation

import com.lola.framework.core.container.Container

open class ContainerNavigatorByName(containers: Collection<Container>) :
    AbstractContainerNavigator<String>(containers) {
    override fun isPair(key: String, value: Container): Boolean {
        return value.name == key
    }
}