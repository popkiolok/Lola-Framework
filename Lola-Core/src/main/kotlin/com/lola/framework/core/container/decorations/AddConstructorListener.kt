package com.lola.framework.core.container.decorations

import com.lola.framework.core.constructor.Constructor
import com.lola.framework.core.container.ContainerDecoration

interface AddConstructorListener : ContainerDecoration {
    fun onConstructorAdded(constructor: Constructor)
}