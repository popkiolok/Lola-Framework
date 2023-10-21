package com.lola.framework.core.decoration

/**
 * A decoration of a container or its component is an object
 * that can store this container or component related information, extend it behavior by listening events.
 */
interface Decoration<T> {
    /**
     * The container or component this decoration is applied to.
     */
    val self: T
}