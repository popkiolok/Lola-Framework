package com.lola.framework.core.container.navigation

import com.lola.framework.core.LClass
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.Test

class ContainerNavigatorByNameTest {
    private val containers = listOf<LClass>()
    private val container = mock<LClass>(name = "Container Under Test")
    private val navigator = ContainerNavigatorByName(containers)

    init {
        whenever(container.name).thenReturn("Container Name")
    }

    @Test
    fun `when isPair then return true if key is name of container`() {
        val result1 = navigator.isPair("Container Name", container)
        val result2 = navigator.isPair(String(StringBuffer("Container Name")), container)

        assertTrue(result1)
        assertTrue(result2)
    }

    @Test
    fun `when isPair then return false if key is not name of container`() {
        val result = navigator.isPair("Not Container Name", container)

        assertFalse(result)
    }
}