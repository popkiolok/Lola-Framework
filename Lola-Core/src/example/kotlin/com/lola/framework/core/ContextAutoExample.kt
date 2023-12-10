package com.lola.framework.core

import com.lola.framework.core.context.Auto

fun main() {
    Lola.initialize()
    ContextAutoExample::class.lola.createInstance { it.register { "Hello, World!" } }
    Lola.printInfo()
}

class ContextAutoExample(@Auto val hello: String) {
    init {
        println(hello)
    }
}