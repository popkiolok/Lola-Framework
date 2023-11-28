package com.lola.framework.setting

import com.lola.framework.core.LClass
import com.lola.framework.core.decoration.getDecoration
import com.lola.framework.core.decoration.hasDecoration
import com.lola.framework.core.LProperty

/**
 * Checks if the [LClass] has a [SettingContainer] decoration.
 *
 * @return true if the [LClass] has a [SettingContainer] decoration, false otherwise.
 */
val LClass.hasSettings: Boolean
    get() = hasDecoration<SettingContainer>()

/**
 * Retrieves the [SettingContainer] decoration from the [LClass].
 *
 * @return The [SettingContainer] decoration, or null if not found.
 */
val LClass.settings: SettingContainer?
    get() = getDecoration<SettingContainer>()

/**
 * Checks if the [LProperty] has a [SettingProperty] decoration.
 *
 * @return true if the [LProperty] has a [SettingProperty] decoration, false otherwise.
 */
val LProperty.hasSetting: Boolean
    get() = hasDecoration<SettingProperty>()

/**
 * Retrieves the [SettingProperty] decoration from the [LProperty].
 *
 * @return The [SettingProperty] decoration, or null if not found.
 */
val LProperty.setting: SettingProperty?
    get() = getDecoration<SettingProperty>()