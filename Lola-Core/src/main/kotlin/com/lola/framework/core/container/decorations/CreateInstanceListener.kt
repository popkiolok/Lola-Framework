package com.lola.framework.core.container.decorations

import com.lola.framework.core.container.ContainerDecoration
import com.lola.framework.core.container.ContainerInstance

interface CreateInstanceListener : ContainerDecoration {
    fun onCreateInstance(instance: ContainerInstance)
}