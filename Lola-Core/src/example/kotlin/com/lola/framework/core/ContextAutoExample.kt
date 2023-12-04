package com.lola.framework.core

import com.lola.framework.core.context.Auto

fun main() {
    Lola.initialize()
    ContextAutoExample::class.lola.createInstance { it.register { "Hello, World!" } }
}

class ContextAutoExample(@Auto val hello: String) {
    init {
        println(hello)
    }
}