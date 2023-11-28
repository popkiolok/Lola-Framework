package com.lola.framework.core.function

import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.LParameter
import com.lola.framework.core.function.parameter.decorations.ParameterValueSupplier
import com.lola.framework.core.impl.AbstractFunction
import com.lola.framework.core.util.Option
import io.github.oshai.kotlinlogging.KotlinLogging
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import org.mockito.kotlin.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val log = KotlinLogging.logger {}

class AbstractFunctionTest {
    private val parameters = ArrayList<LParameter>()
    private val abstractFunction = spy<AbstractFunction>()
    private val ci = ContainerInstance(mock())
    private val params = Int2ObjectArrayMap<Any?>(mapOf(0 to 0, 2 to 2))

    init {
        whenever(abstractFunction(any(), anyVararg())).then {
            val args = (it.rawArguments[1] as Array<*>).joinToString()
            log.trace { "Calling AbstractFunction with args $args." }
            args
        }

        for (i in 0..<3) parameters += mock<LParameter>()
        whenever(abstractFunction.parameters).thenReturn(parameters)
    }

    @Test
    fun `when tryInvoke with missing parameter and present initializer then use initializer`() {
        val initializer = getInitializer(true)
        whenever(parameters[1].valueSuppliers).thenReturn(listOf(initializer))

        val result = abstractFunction.tryInvoke(ci, params)

        assertEquals("0, 1, 2", result.get())
    }

    @Test
    fun `when tryInvoke with missing parameter and present initializer that can't initialize then return empty option`() {
        val initializer = getInitializer(false)
        whenever(parameters[1].valueSuppliers).thenReturn(listOf(initializer))

        val result = abstractFunction.tryInvoke(ci, params)

        assertTrue(result.isEmpty)
    }

    @Test
    fun `when tryInvoke with missing parameter and no initializer for it then return empty option`() {
        val result = abstractFunction.tryInvoke(ci, params)

        assertTrue(result.isEmpty)
    }

    private fun getInitializer(canInitialize: Boolean): ParameterValueSupplier {
        val r = mock<ParameterValueSupplier>()
        whenever(r.supplyValue(any())).thenReturn(if (canInitialize) Option(1) else Option.empty())
        return r
    }
}