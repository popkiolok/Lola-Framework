package com.lola.framework.core.container

import com.lola.framework.core.property.Property
import com.lola.framework.core.property.decorations.PropertyValueSupplier
import com.lola.framework.core.util.Option
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.whenever

class ContainerInstanceTest {
    private val container = mock<Container>(name = "Container Under Test")

    private val property1 = mock<Property>(name = "Property Under Test #1")
    private var p1Value: Any? = null
    private val property2 = mock<Property>(name = "Property Under Test #2")

    private val workInitializer = mock<PropertyValueSupplier>(
        name = "Property Initializer That Can Initialize"
    )
    private val notWorkInitializer = mock<PropertyValueSupplier>(
        name = "Property Initializer That Can't Initialize"
    )

    init {
        whenever(container.properties).thenReturn(listOf(property1, property2))
        whenever(property1.initializers).thenReturn(listOf(workInitializer))
        whenever(property1.set(any(), same(Value1))).then {
            p1Value = it.arguments[1]
            return@then Unit
        }
        whenever(property2.initializers).thenReturn(listOf(notWorkInitializer))
        whenever(workInitializer.supplyValue(any())).thenReturn(Option(Value1))
        whenever(notWorkInitializer.supplyValue(any())).thenReturn(Option.empty())
    }

    @Test
    fun `when create ContainerInstance then initialize property using initializer that can initialize`() {
        ContainerInstance(container).instance = Any()

        assertEquals(Value1, p1Value)
    }

    @Test
    fun `when create ContainerInstance then don't initialize property using initializer that can't initialize`() {
        whenever(property2.set(any(), any())).thenThrow(IllegalStateException())

        assertDoesNotThrow {
            ContainerInstance(container).instance = Any()
        }
    }

    object Value1
}