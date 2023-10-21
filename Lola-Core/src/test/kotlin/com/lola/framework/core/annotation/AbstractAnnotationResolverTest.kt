package com.lola.framework.core.annotation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KClass

class AbstractAnnotationResolverTest {
    private val testAnn = TestAnn1()
    private val annotated = object : AbstractAnnotationResolver() {
        @Suppress("WARNINGS")
        override fun <T : Annotation> findAnnotation(ann: KClass<out T>): T? {
            return if (ann == TestAnn1::class) {
                testAnn as T
            } else null
        }

    }

    @Test
    fun `test hasAnnotation`() {
        assertFalse(annotated.hasAnnotation(TestAnn2::class))
        assertTrue(annotated.hasAnnotation(TestAnn1::class))
    }

    @Test
    fun `test getAnnotation`() {
        assertThrows<NullPointerException> { annotated.getAnnotation(TestAnn2::class) }
        assertEquals(testAnn, annotated.getAnnotation(TestAnn1::class))
    }

    annotation class TestAnn1
    annotation class TestAnn2
}