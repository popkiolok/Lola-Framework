package com.lola.framework.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PathTest {
    @Test
    fun `test parent with multiple nodes`() {
        // Arrange
        val path = Path(listOf(1, 2, 3))

        // Act
        val parent = path.parent

        // Assert
        assertEquals(Path(listOf(1, 2)), parent)
    }

    @Test
    fun `test parent with single node`() {
        // Arrange
        val path = Path(listOf(1))

        // Act
        val parent = path.parent

        // Assert
        assertNull(parent)
    }

    @Test
    fun `test lastNode`() {
        // Arrange
        val path = Path(listOf("A", "B", "C"))

        // Act
        val lastNode = path.lastNode

        // Assert
        assertEquals("C", lastNode)
    }

    @Test
    fun `plus should connect two paths into one`() {
        val nodes1 = listOf("A", "B")
        val nodes2 = listOf("C", "D")
        val path1 = Path(nodes1)
        val path2 = Path(nodes2)

        val result = path1 + path2

        assertEquals(Path(nodes1 + nodes2), result)
    }

    @Test
    fun `plus should connect path with a node`() {
        val nodes = listOf("A", "B")
        val path = Path(nodes)
        val nextNode = "C"

        val result = path + nextNode

        assertEquals(Path(nodes + nextNode), result)
    }

    @Test
    fun `toString should return string representation of the path with default delimiter`() {
        val nodes = listOf("A", "B", "C")
        val path = Path(nodes)

        val result = path.toString()

        assertEquals("A.B.C", result)
    }

    @Test
    fun `toString should return string representation of the path with custom delimiter`() {
        val nodes = listOf("A", "B", "C")
        val path = Path(nodes)
        val delimiter = "/"

        val result = path.toString(delimiter)

        assertEquals("A/B/C", result)
    }

    @Test
    fun `contains should return true if the path contains the specified node`() {
        val nodes = listOf("A", "B", "C")
        val path = Path(nodes)
        val node = "B"

        val result = path.contains(node)

        assertTrue(result)
    }

    @Test
    fun `contains should return false if the path does not contain the specified node`() {
        val nodes = listOf("A", "B", "C")
        val path = Path(nodes)
        val node = "D"

        val result = path.contains(node)

        assertFalse(result)
    }

    @Test
    fun `length should return the number of nodes in the path`() {
        val nodes = listOf("A", "B", "C")
        val path = Path(nodes)

        val result = path.length

        assertEquals(nodes.size, result)
    }

    @Suppress("WARNINGS")
    @Test
    fun `test equals`() {
        val path = Path(listOf("A", "B", "C"))
        assertTrue(path == path)
        assertTrue(Path(listOf("A", "B", "C")) == Path(listOf("A", "B", "C")))
        assertFalse(Path(listOf("A", "B", "C")) == Path(listOf("A", "B", "D")))
        assertFalse(Path(listOf("A", "B", "C")) == Path(listOf("A", "B")))
        assertFalse(Path(listOf("A", "B", "C")) == null)
        assertFalse(Path(listOf("A", "B", "C")) == Any())
    }

    @Test
    fun `given 2 different path objects with the same nodes when invoke hashCode then return same hash codes`() {
        val path1 = Path(listOf("A", "B", "C"))
        val path2 = Path(listOf("A", "B", "C"))

        val hash1 = path1.hashCode()
        val hash2 = path2.hashCode()

        assertEquals(hash1, hash2)
    }

    @Test
    fun `given 2 different path objects with different nodes when invoke hashCode then return different hash codes`() {
        val path1 = Path(listOf("A", "B", "C"))
        val path2 = Path(listOf("A", "B", "D"))

        val hash1 = path1.hashCode()
        val hash2 = path2.hashCode()

        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `test empty path`() {
        val emptyPath = emptyPath<String>()

        assertTrue(emptyPath.length == 0)
        assertTrue(emptyPath.parent == null)
        assertThrows<NoSuchElementException> { emptyPath.lastNode }
        assertTrue(emptyPath === emptyPath<Int>())
    }
}