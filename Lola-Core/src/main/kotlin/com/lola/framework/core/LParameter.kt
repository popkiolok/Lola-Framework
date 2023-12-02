package com.lola.framework.core

import kotlin.reflect.KParameter

class LParameter(override val self: KParameter, val holder: LCallable<*, *>) : LAnnotatedElement()