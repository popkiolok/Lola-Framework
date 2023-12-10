package com.lola.framework.setting

import com.lola.framework.core.LAnnotatedElement
import com.lola.framework.core.LCallable
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.ForAnnotated
import kotlin.reflect.KProperty

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Setting(val name: String, val info: String = "")

@ForAnnotated(Setting::class)
class SettingProperty(override val target: LAnnotatedElement, val data: Setting) :
    Decoration<LAnnotatedElement>