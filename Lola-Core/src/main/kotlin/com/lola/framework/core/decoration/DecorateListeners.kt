package com.lola.framework.core.decoration

interface DecorateListener<T : Decorated> : Decoration<T> {
    fun onDecorated(decoration: Decoration<T>)
}