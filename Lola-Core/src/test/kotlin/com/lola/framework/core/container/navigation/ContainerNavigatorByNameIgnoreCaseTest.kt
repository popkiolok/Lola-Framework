package com.lola.framework.core.container.navigation

import com.lola.framework.core.LClass
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ContainerNavigatorByNameIgnoreCaseTest {
    private val container = mock<LClass>(name = "Container Under Test")
    private val containers = listOf(container)
    private val navigator = ContainerNavigatorByNameIgnoreCase(containers)

    init {
        whenever(container.name).thenReturn("Container Name")
    }

    @Test
    fun `when get then return container by name ignore case`() {
        for (i in 0..1) {
            val result1 = navigator["Container Name"]
            val result2 = navigator["conTAINer naMe"]

            assertEquals(container, result1)
            assertEquals(container, result2)
        }
    }

    @Test
    fun `when get for not existing container name then throw NoSuchElementException`() {
        for (i in 0..1) {
            assertThrows<NoSuchElementException> {
                navigator["Not Container Name"]
            }
        }
    }

    @Test
    fun `when isPair then return true if key is name of container ignore case`() {
        val result1 = navigator.isPair("Container Name", container)
        val result2 = navigator.isPair("conTAINer naMe", container)

        assertTrue(result1)
        assertTrue(result2)
    }

    @Test
    fun `when isPair then return false if key is not name of container`() {
        val result = navigator.isPair("Not Container Name", container)

        assertFalse(result)
    }
}