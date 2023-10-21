package com.lola.framework.core.decoration

import com.google.common.collect.Multimap
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

class AbstractDecoratedTest {
    private val decorated = TestDecorated()

    @Test
    fun `when hasDecoration with existing decoration then return true`() {
        decorated.decorations1.put(TestDecoration::class, TestDecoration(decorated))

        assertTrue(decorated.hasDecoration<TestDecoration>())
    }

    @Test
    fun `when hasDecoration with non-existing decoration then return false`() {
        assertFalse(decorated.hasDecoration<TestDecoration>())
    }

    @Test
    fun `when getDecorations with existing decoration then return list with them`() {
        val decoration = TestDecoration(decorated)

        decorated.decorations1.put(TestDecoration::class, decoration)
        val decorations = decorated.getDecorations<TestDecoration>()

        assertEquals(1, decorations.size)
        assertEquals(decoration, decorations.first())
    }

    @Test
    fun `when getDecorations with non-existing decoration then return empty list`() {
        val decorations = decorated.getDecorations<TestDecoration>()

        assertTrue(decorations.isEmpty())
    }

    @Test
    fun `when getDecoration with existing decoration then return it`() {
        val decoration = TestDecoration(decorated)

        decorated.decorations1.put(TestDecoration::class, decoration)
        val result = decorated.getDecoration<TestDecoration>()

        assertEquals(decoration, result)
    }

    @Test
    fun `when getDecoration with non-existing decoration then return null`() {
        val result = decorated.getDecoration<TestDecoration>()

        assertNull(result)
    }

    @Test
    fun `when decorate then add decoration for it class and all its superclasses`() {
        val decoration = TestDecoration(decorated)

        decorated.decorate(decoration)
        val result = sequenceOf(
            TestDecoration::class, TestDecorationSuperClass1::class,
            TestDecorationSuperClass::class
        ).map { decorated.decorations[it] }

        assertTrue(result.none { it.isEmpty() })
    }

    class TestDecorated : AbstractDecorated<Decoration<*>>() {
        val decorations1: Multimap<KClass<out Decoration<*>>, Decoration<*>>
            get() = super.decorations0
    }

    interface TestDecorationSuperClass : Decoration<TestDecorated>

    interface TestDecorationSuperClass1 : TestDecorationSuperClass

    class TestDecoration(override val self: TestDecorated) : TestDecorationSuperClass1
}