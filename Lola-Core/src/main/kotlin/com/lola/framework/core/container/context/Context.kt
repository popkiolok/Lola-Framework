package com.lola.framework.core.container.context

import com.lola.framework.core.log
import javax.annotation.CheckReturnValue
import javax.annotation.meta.When

/**
 * Stores framework and application level context objects.
 */
class Context(val parents: MutableCollection<Context>) {
    private val suppliers: MutableMap<Any, () -> Any?> = HashMap()

    /**
     * Register context value supplier for specified keys.
     *
     * @param keys Unique keys for value in context.
     * @param supplier Function that supplies value.
     * @return this [Context] instance.
     */
    @CheckReturnValue(`when` = When.MAYBE)
    fun register(vararg keys: Any, supplier: () -> Any?): Context {
        keys.forEach { suppliers += it to supplier }
        return this
    }

    fun has(key: Any): Boolean {
        return suppliers.containsKey(key) || parents.any { it.has(key) }
    }

    /**
     * Gets value by key from this and parent contexts.
     *
     * @param key Unique key for value in context to provide value by.
     *
     * @return Value from this context, if present, otherwise value from parent context.
     */
    operator fun get(key: Any): Any? {
        return if (suppliers.containsKey(key)) {
            suppliers[key]!!.invoke()
        } else {
            val parent = parents.firstOrNull { it.has(key) }
            if (parent == null) {
                log.error { "Unable to get value from context $this." }
                log.error { "No value present in this or parent context for key $key." }
                throw NoSuchElementException("No value present in this or parent contexts for key.")
            } else parent[key]
        }
    }
}