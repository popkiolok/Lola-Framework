package com.lola.framework.core.container

import com.lola.framework.core.constructor.Constructor
import com.lola.framework.core.LParameter
import com.lola.framework.core.impl.AbstractClass
import com.lola.framework.core.toJSON
import com.lola.framework.core.util.Option
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.spy
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

private val log = KotlinLogging.logger {}

class AbstractContainerTest {
    private val abstractContainer = spy<TestContainer>()
    private val containerWithNoDefaultConstructor = spy<TestContainer>()

    private val noParamsConstructor = mock<Constructor>(name = "No Parameters Constructor")

    private val parameter1 = mock<LParameter>(name = "Parameter 1")
    private val oneParamConstructor = mock<Constructor>(name = "One Parameter Constructor")

    init {
        whenever(noParamsConstructor.parameters).thenReturn(emptyList())
        whenever(noParamsConstructor.tryInvoke(any(), any())).thenReturn(Option(Instance1))

        whenever(oneParamConstructor.parameters).thenReturn(listOf(parameter1))
        whenever(oneParamConstructor.tryInvoke(any(), any())).then {
            log.trace { "Invoke with args ${it.arguments.toJSON()}." }
        }
        whenever(oneParamConstructor.tryInvoke(any(), argThat { isNotEmpty() })).thenReturn(Option(Instance2))
        whenever(oneParamConstructor.tryInvoke(any(), argThat { isEmpty() })).thenReturn(Option.empty())

        abstractContainer.elements.putAll(
            Constructor::class,
            listOf(oneParamConstructor, noParamsConstructor)
        )
        containerWithNoDefaultConstructor.elements.putAll(
            Constructor::class,
            listOf(oneParamConstructor)
        )
    }

    @Test
    fun `when createInstance with no parameters then create a ContainerInstance with constructor takes no parameters`() {
        val params: Map<LParameter, Any?> = mapOf()

        val cInstance = abstractContainer.createInstance(params)

        assertEquals(Instance1, cInstance.instance)
    }

    @Test
    fun `when createInstance with one parameter then create a ContainerInstance with constructor takes one parameter`() {
        val params = mapOf(parameter1 to Any())

        val cInstance = abstractContainer.createInstance(params)

        assertEquals(Instance2, cInstance.instance)
    }

    @Test
    fun `when createInstance with no parameters on container with no default constructor then throw exception`() {
        val params: Map<LParameter, Any?> = mapOf()

        assertThrows<NullPointerException> {
            containerWithNoDefaultConstructor.createInstance(params)
        }
    }

    @Test
    fun `when getContainerInstance after creating instance then return correct ContainerInstance object`() {
        val params: Map<LParameter, Any?> = mapOf()

        val cInstance = abstractContainer.createInstance(params)

        assertEquals(cInstance, getContainerInstance(cInstance.instance))
    }

    @Test
    fun `when getContainerInstance for null instance then return null`() {
        assertEquals(null, getContainerInstance(null))
    }

    abstract class TestContainer : AbstractClass() {
        override val name: String
            get() = "TestContainer"
        override val isFinal: Boolean
            get() = true
    }

    object Instance1
    object Instance2
}