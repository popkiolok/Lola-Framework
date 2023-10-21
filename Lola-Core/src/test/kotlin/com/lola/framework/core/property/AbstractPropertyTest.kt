package com.lola.framework.core.property

import com.lola.framework.core.Type
import com.lola.framework.core.annotation.AnnotationResolver
import com.lola.framework.core.container.Container
import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.property.decorations.PropertyGetListener
import com.lola.framework.core.property.decorations.PropertySetListener
import com.lola.framework.core.util.Option
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AbstractPropertyTest {
    private val mProperty = TestProperty(true)
    private val immProperty = TestProperty(false)
    private val ci = ContainerInstance(mock<Container>()).also { it.instance = Any() }

    @Test
    fun `test get`() {
        val result = immProperty[ci]

        assertEquals(immProperty.value, result)
    }

    @Test
    fun `test get with listener`() {
        val listener = mock<PropertyGetListener>(name = "Test Property Get Listener")
        whenever(listener.onPropertyGet(any(), any())).thenReturn("2")
        immProperty.decorate(listener)

        val result = immProperty[ci]

        assertEquals("2", result)
    }

    @Test
    fun `test set immutable`() {
        assertThrows<IllegalStateException> {
            immProperty[ci] = Any()
        }
    }

    @Test
    fun `test set mutable`() {
        mProperty[ci] = "2"

        assertEquals("2", mProperty.value)
    }

    @Test
    fun `test set mutable with cancelling listener`() {
        val value2 = "2"
        val listener = mock<PropertySetListener>(name = "Test Cancelling Property Set Listener")
        whenever(listener.onPropertySet(any(), any())).thenReturn(Option.empty())
        mProperty.decorate(listener)

        mProperty[ci] = value2

        assertEquals("1", mProperty.value)
    }

    @Test
    fun `test set mutable with change value listener`() {
        val listener = mock<PropertySetListener>(name = "Test Change Value Property Set Listener")
        whenever(listener.onPropertySet(any(), any())).thenReturn(Option("3"))
        mProperty.decorate(listener)

        mProperty[ci] = "2"

        assertEquals("3", mProperty.value)
    }

    class TestProperty(override val mutable: Boolean) : AbstractProperty() {
        override val name: String = "testProperty"
        override val type: Type = mock<Type>()
        override val parameters: Collection<Parameter> = emptyList()

        var value: Any? = "1"

        override fun getValue(instance: ContainerInstance): Any? {
            return value
        }

        override fun setValue(instance: ContainerInstance, value: Any?) {
            this.value = value
        }

        override val defaultValue: Option<Any?>
            get() = Option.empty()
        override val annotations: AnnotationResolver
            get() = mock<AnnotationResolver>()
    }
}