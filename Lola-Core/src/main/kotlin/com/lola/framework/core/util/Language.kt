package com.lola.framework.core.util

fun <T, R> equalsBy(a: T, b: T, mapper: (T) -> R): Boolean {
    return mapper(a) == mapper(b)
}

fun <T> both(a: T, b: T, predicate: (T) -> Boolean): Boolean {
    return predicate(a) && predicate(b)
}