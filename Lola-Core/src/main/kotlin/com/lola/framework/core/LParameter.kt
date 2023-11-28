package com.lola.framework.core

import com.lola.framework.core.LType
import com.lola.framework.core.decoration.Decorated
import kotlin.reflect.KParameter

/**
 * TODO
 */
interface LParameter : KParameter, Decorated {
    /**
     * Name of this parameter as it was declared in the source code,
     * if the parameter has name and its name is available at runtime.
     * If [kind] of the parameter is [KParameter.Kind.INSTANCE] or [KParameter.Kind.EXTENSION_RECEIVER], returns `INSTANCE`.
     * If the original name is not available at runtime, returns `<PARAM` + [index]`>` as name.
     */
    override val name: String

    override val type: LType
}