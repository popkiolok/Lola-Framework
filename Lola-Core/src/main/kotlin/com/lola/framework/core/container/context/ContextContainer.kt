package com.lola.framework.core.container.context

import com.lola.framework.core.constructor.Constructor
import com.lola.framework.core.container.Container
import com.lola.framework.core.container.decorations.AddConstructorListener
import com.lola.framework.core.container.decorations.AddPropertyListener
import com.lola.framework.core.property.Property

class ContextContainer(override val self: Container) : AddPropertyListener, AddConstructorListener {

    override fun onPropertyAdded(property: Property) {
        if (property.annotations.hasAnnotation(Auto::class))
            property.decorate(AutoProperty(property))
    }

    override fun onConstructorAdded(constructor: Constructor) {
        constructor.parameters.forEach { param ->
            if (param.annotations.hasAnnotation(Auto::class)) {
                param.decorate(AutoParameter(param))
            }
        }
    }
}