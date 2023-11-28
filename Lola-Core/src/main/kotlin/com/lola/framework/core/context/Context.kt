package com.lola.framework.core.context

import com.lola.framework.core.log
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Stores framework and application level context objects.
 */
class Context(val parents: MutableCollection<Context> = ArrayList()) {
    private val suppliers: MutableMap<Any, () -> Any?> = HashMap()

    /**
     * Register context value supplier for specified keys.
     *
     * @param keys Unique keys for value in context.
     * @param supplier Function that supplies value.
     */
    fun register(vararg keys: Any, supplier: () -> Any?) {
        keys.forEach { suppliers += it to supplier }
    }

    /**
     * Register context value supplier of type [T] for three keys: simple class name, kotlin class, java class.
     *
     * @param supplier Function that supplies value.
     */
    inline fun <reified T : Any> register(crossinline supplier: () -> T?) {
        register(T::class.simpleName ?: T::class.java.simpleName, T::class, T::class.java) { supplier() }
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

    operator fun <T : Any> get(key: KClass<T>): T? {
        return get(key as Any)?.let { key.cast(it) }
    }

    inline fun <reified T : Any> get(): T? {
        return get(T::class)
    }
}