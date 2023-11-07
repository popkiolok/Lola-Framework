package com.lola.framework.module

import com.lola.framework.core.container.Container
import com.lola.framework.core.decoration.getDecoration
import com.lola.framework.core.decoration.hasDecoration

val Container.isModule: Boolean
    get() = hasDecoration<ModuleContainer>()

val Container.asModule: ModuleContainer
    get() = getDecoration<ModuleContainer>() ?: throw NullPointerException("Container '$this' is not a module.")