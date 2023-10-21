package com.lola.framework.module

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Module(val group: String, val path: String, val info: String = "")
