package com.lola.framework.setting

import com.lola.framework.core.property.Property
import com.lola.framework.core.property.PropertyDecoration

class SettingProperty(
    override val self: Property,
    /**
     * Setting simple name.
     */
    val name: String,

    /**
     * Setting description.
     */
    val info: String
) : PropertyDecoration