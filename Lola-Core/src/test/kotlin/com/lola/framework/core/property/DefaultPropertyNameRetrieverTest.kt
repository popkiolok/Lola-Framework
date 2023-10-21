package com.lola.framework.core.property

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private val log = KotlinLogging.logger {}

class DefaultPropertyNameRetrieverTest {
    @Test
    fun `when getPropertyName then return property name for accessor name`() {
        listOf(
            "getTest", "setTest", "isTest", "GetTest", "SetTest", "IsTest", "get_Test",
            "set_Test", "is_Test", "get_test", "set_test", "is_test", "Get_test", "Set_test",
            "Is_test", "Get_Test", "Set_Test", "Is_Test"
        ).forEach {
            log.trace { "Getting property name from accessor name $it." }
            val result = DefaultPropertyNameRetriever.getPropertyName(it)

            assertEquals("Test", result)
        }
    }
}