package com.lola.framework.core

import com.lola.framework.core.decoration.Decorated
import kotlin.reflect.KParameter

class LParameter(override val self: KParameter, val holder: LCallable<*, *>) : LAnnotatedElement()