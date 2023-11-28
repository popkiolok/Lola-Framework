package com.lola.framework.core.container.navigation

import com.lola.framework.core.LClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock

class AbstractContainerNavigatorTest {
    private val container = mock<LClass>(name = "Container Under Test")
    private val containers = listOf(container)

    private val abstractContainerNavigator = object : AbstractContainerNavigator<Any>(containers) {
        override fun isPair(key: Any, value: LClass) = value.hashCode() == key
    }

    @Test
    fun `get should return the container associated with the given key`() {
        for (i in 0..1) {
            val result = abstractContainerNavigator[container.hashCode()]

            assertEquals(container, result)
        }
    }

    @Test
    fun `get should throw NoSuchElementException if no container is found for the given key`() {
        assertThrows<NoSuchElementException> { abstractContainerNavigator[0] }
    }
}