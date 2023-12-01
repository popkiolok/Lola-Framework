package com.lola.framework.module

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Module(val name: String = "", val info: String = "")