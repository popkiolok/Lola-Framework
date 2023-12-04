package com.lola.framework.core

import com.lola.framework.core.decoration.DecorateListener
import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.decoration.Decoration
import kotlin.reflect.KParameter

class LParameter(override val self: KParameter, val holder: LCallable<*, *>) : LAnnotatedElement() {

    override fun <T : Decorated> decorate(decoration: Decoration<T>) {
        super.decorate(decoration)
        holder.onDecoratedParameter(decoration as Decoration<LParameter>)
    }
}