package com.lola.framework.core.util

/**
 * Optional value that can be null and present, can be not null and can be not present.
 * Forked from Java optional.
 *
 * @param T Value type.
 *
 * @param value The value.
 */
@JvmInline
value class Option<out T>(val value: T) {
    /**
     * If a value is present, returns the value, otherwise throws
     * `NoSuchElementException`.
     *
     * @return the value described by this `Optional`
     * @throws NoSuchElementException if no value is present
     */
    fun get(): T {
        if (value === NotPresent) {
            throw NoSuchElementException("No value present")
        }
        return value
    }

    val isPresent: Boolean
        /**
         * If a value is present, returns `true`, otherwise `false`.
         *
         * @return `true` if a value is present, otherwise `false`
         */
        get() = value !== NotPresent
    val isEmpty: Boolean
        /**
         * If a value is  not present, returns `true`, otherwise
         * `false`.
         *
         * @return  `true` if a value is not present, otherwise `false`
         */
        get() = value === NotPresent

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     *
     * @param action the action to be performed, if a value is present
     */
    inline fun ifPresent(action: (T) -> Unit) {
        if (isPresent) {
            action(value)
        }
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the given empty-based action.
     *
     * @param action the action to be performed, if a value is present
     * @param emptyAction the empty-based action to be performed, if no value is
     * present
     */
    inline fun ifPresentOrElse(action: (T) -> Unit, emptyAction: () -> Unit) {
        if (isPresent) {
            action(value)
        } else {
            emptyAction()
        }
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns an `Optional` describing the value, otherwise returns an
     * empty `Optional`.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return an `Optional` describing the value of this
     * `Optional`, if a value is present and the value matches the
     * given predicate, otherwise an empty `Optional`
     */
    inline fun filter(predicate: (T) -> Boolean): Option<T> {
        return if (isEmpty) {
            this
        } else {
            if (predicate(value)) this else empty()
        }
    }

    /**
     * If a value is present, returns an `Optional` describing
     * the result of applying the given mapping function to
     * the value, otherwise returns an empty `Optional`.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param U The type of the value returned from the mapping function
     * @return an `Optional` describing the result of applying a mapping
     * function to the value of this `Optional`, if a value is
     * present, otherwise an empty `Optional`
     */
    inline fun <U> map(mapper: (T) -> U): Option<U> {
        return if (isEmpty) {
            empty()
        } else {
            Option(mapper(value))
        }
    }

    /**
     * If a value is present, returns the result of applying the given
     * `Optional`-bearing mapping function to the value, otherwise returns
     * an empty `Optional`.
     *
     *
     * This method is similar to [.map], but the mapping
     * function is one whose result is already an `Optional`, and if
     * invoked, `flatMap` does not wrap it within an additional
     * `Optional`.
     *
     * @param U The type of value of the `Optional` returned by the
     * mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying an `Optional`-bearing mapping
     * function to the value of this `Optional`, if a value is
     * present, otherwise an empty `Optional`
     */
    inline fun <U> flatMap(mapper: (T) -> Option<U>): Option<U> {
        return if (isEmpty) {
            empty()
        } else {
            mapper(value)
        }
    }

    /**
     * If a value is present, returns an `Optional` describing the value,
     * otherwise returns an `Optional` produced by the supplying function.
     *
     * @param supplier the supplying function that produces an `Optional`
     * to be returned
     * @return returns an `Optional` describing the value of this
     * `Optional`, if a value is present, otherwise an
     * `Optional` produced by the supplying function.
     */
    inline fun or(supplier: () -> Option<@UnsafeVariance T>): Option<T> {
        return if (isPresent) {
            this
        } else {
            supplier()
        }
    }

    /**
     * If a value is present, returns a [Sequence] containing
     * only that value, otherwise returns an empty `Sequence`.
     *
     * @return the optional value as a `Sequence`
     */
    fun sequence(): Sequence<T> {
        return if (isEmpty) {
            emptySequence()
        } else {
            sequenceOf(value)
        }
    }

    /**
     * If a value is present, returns the value, otherwise returns
     * `other`.
     *
     * @param other the value to be returned, if no value is present.
     * @return the value, if present, otherwise `other`
     */
    fun orElse(other: @UnsafeVariance T): T {
        return value ?: other
    }

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     *
     * @param supplier the supplying function that produces a value to be returned
     * @return the value, if present, otherwise the result produced by the
     * supplying function
     * @throws NullPointerException if no value is present and the supplying
     * function is `null`
     */
    inline fun orElseGet(supplier: () -> @UnsafeVariance T): T {
        return value ?: supplier()
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * `NoSuchElementException`.
     *
     * @return the non-`null` value described by this `Optional`
     * @throws NoSuchElementException if no value is present
     */
    fun orElseThrow(): T {
        if (isEmpty) {
            throw NoSuchElementException("No value present")
        }
        return value
    }

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @param X Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an
     * exception to be thrown
     * @return the value, if present
     * @throws X if no value is present
     */
    inline fun <X : Throwable> orElseThrow(exceptionSupplier: () -> X): T {
        return if (isPresent) value else throw exceptionSupplier()
    }

    /**
     * Returns a non-empty string representation of this `Optional`
     * suitable for debugging.  The exact presentation format is unspecified and
     * may vary between implementations and versions.
     *
     * @implSpec
     * If a value is present the result must include its string representation
     * in the result.  Empty and present `Optional`s must be unambiguously
     * differentiable.
     *
     * @return the string representation of this instance
     */
    override fun toString(): String {
        return if (value !== NotPresent) "Optional[$value]" else "Optional.empty"
    }

    companion object {
        private val NotPresent = Any()

        /**
         * Common instance for `empty()`.
         */
        private val EMPTY: Option<*> = Option<Any?>(NotPresent)

        /**
         * Returns an empty `Optional` instance. No value is present for this
         * `Optional`.
         *
         * @param T type of the non-existent value
         * @return an empty `Optional`
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> empty(): Option<T> {
            return EMPTY as Option<T>
        }
    }
}
