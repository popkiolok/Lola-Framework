package com.lola.framework.setting

import com.lola.framework.core.container.Container
import com.lola.framework.core.decoration.getDecoration
import com.lola.framework.core.decoration.hasDecoration
import com.lola.framework.core.property.Property

/**
 * Checks if the [Container] has a [SettingContainer] decoration.
 *
 * @return true if the [Container] has a [SettingContainer] decoration, false otherwise.
 */
val Container.hasSettings: Boolean
    get() = hasDecoration<SettingContainer>()

/**
 * Retrieves the [SettingContainer] decoration from the [Container].
 *
 * @return The [SettingContainer] decoration, or null if not found.
 */
val Container.settings: SettingContainer?
    get() = getDecoration<SettingContainer>()

/**
 * Checks if the [Property] has a [SettingProperty] decoration.
 *
 * @return true if the [Property] has a [SettingProperty] decoration, false otherwise.
 */
val Property.hasSetting: Boolean
    get() = hasDecoration<SettingProperty>()

/**
 * Retrieves the [SettingProperty] decoration from the [Property].
 *
 * @return The [SettingProperty] decoration, or null if not found.
 */
val Property.setting: SettingProperty?
    get() = getDecoration<SettingProperty>()