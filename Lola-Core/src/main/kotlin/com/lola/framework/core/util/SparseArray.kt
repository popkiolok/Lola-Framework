package com.lola.framework.core.util

typealias SparseArray<T> = Array<T>

val PLACEHOLDER = Any()

inline fun <T> SparseArray<T>.complete(mapper: (Int) -> T) {
    forEachIndexed { index, e ->
        if (e === PLACEHOLDER) {
            this[index] = mapper(index)
        }
    }
}