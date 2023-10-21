package com.lola.framework.module

import com.lola.framework.core.constructor.Constructor
import com.lola.framework.core.container.decorations.AddConstructorListener
import com.lola.framework.core.container.decorations.AddPropertyListener
import com.lola.framework.core.property.Property

interface DependenciesUsingContainer : AddPropertyListener, AddConstructorListener {

    override fun onPropertyAdded(property: Property) {
        if (property.annotations.hasAnnotation(Dep::class)) {
            property.decorate(DependencyProperty(property))
        }
    }

    override fun onConstructorAdded(constructor: Constructor) {
        constructor.parameters.forEach { param ->
            if (param.annotations.hasAnnotation(Dep::class)) {
                param.decorate(DependencyParameter(param))
            }
        }
    }
}