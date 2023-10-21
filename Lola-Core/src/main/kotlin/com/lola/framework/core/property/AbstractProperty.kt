package com.lola.framework.core.property

import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.decoration.AbstractDecorated
import com.lola.framework.core.decoration.getDecorations
import com.lola.framework.core.property.decorations.PropertyGetListener
import com.lola.framework.core.property.decorations.PropertySetListener
import com.lola.framework.core.property.decorations.PropertyValueSupplier

abstract class AbstractProperty : Property, AbstractDecorated<PropertyDecoration>() {
    override val initializers: Collection<PropertyValueSupplier>
        get() {
            return getDecorations<PropertyValueSupplier>()
        }

    override fun get(instance: ContainerInstance): Any? {
        var value = getValue(instance)
        getDecorations<PropertyGetListener>().forEach { value = it.onPropertyGet(instance, value) }
        return value
    }

    override fun set(instance: ContainerInstance, value: Any?) {
        if (immutable) {
            throw IllegalStateException("Property $this is immutable.")
        }
        var v = value
        getDecorations<PropertySetListener>().forEach {
            val result = it.onPropertySet(instance, v)
            result.ifPresent { nv ->
                v = nv
                return@forEach
            }
            return
        }
        setValue(instance, v)
    }

    protected abstract fun getValue(instance: ContainerInstance): Any?

    protected abstract fun setValue(instance: ContainerInstance, value: Any?)
}