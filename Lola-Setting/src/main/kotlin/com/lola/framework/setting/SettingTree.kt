package com.lola.framework.setting

import com.fasterxml.jackson.annotation.JsonProperty
import com.lola.framework.core.LClass
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.findDecoration
import com.lola.framework.core.decoration.getDecoration
import com.lola.framework.core.getSubclasses
import com.lola.framework.core.lola
import com.lola.framework.core.objectContext
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

abstract class SettingNode(@JsonProperty("name") @field:JsonProperty("name") val name: String) {
    fun getValue(context: Context): Any? {
        return when (this) {
            is SettingSimpleNode -> value

            is SettingSubTreeNode -> {
                val clazz = Class.forName(className).kotlin.lola.getDecoration<SettingContainingClass<Any>>()
                clazz.createWithSettings(context, tree)
            }

            else -> throw UnsupportedOperationException()
        }
    }
}

interface SettingMultiNode {
    val trees: Map<String, Collection<SettingNode>>
}

open class SettingSimpleNode(
    @JsonProperty("name") name: String,
    @JsonProperty("value") @field:JsonProperty("value") val value: Any?
) : SettingNode(name)

class SettingSimpleMultiSubTreeNode(
    @JsonProperty("name") name: String,
    @JsonProperty("value") value: Any?,
    @JsonProperty("trees") @field:JsonProperty("trees") override val trees: Map<String, Collection<SettingNode>>
) : SettingSimpleNode(name, value), SettingMultiNode

open class SettingSubTreeNode(
    @JsonProperty("name") name: String,
    @JsonProperty("className") @field:JsonProperty("className") val className: String,
    @JsonProperty("tree") @field:JsonProperty("tree") val tree: Collection<SettingNode>
) : SettingNode(name)

class SettingMultiSubTreeNode(
    @JsonProperty("name") name: String,
    @JsonProperty("className") className: String,
    @JsonProperty("tree") tree: Collection<SettingNode>,
    @JsonProperty("trees") @field:JsonProperty("trees") override val trees: Map<String, Collection<SettingNode>>
) : SettingSubTreeNode(name, className, tree), SettingMultiNode

operator fun Collection<SettingNode>.get(name: String) = firstOrNull { it.name == name }

operator fun Collection<SettingNode>.get(setting: SettingReference<*>) = get(setting.data.name)

fun settingNodeOf(name: String, value: Any?): SettingNode {
    val valueClass = value?.let { value::class.lola.findDecoration<SettingContainingClass<Any>>() }
    return if (valueClass != null) {
        val subtree = valueClass.getValues(value, false)
        SettingSubTreeNode(name, valueClass.target.self.jvmName, subtree)
    } else {
        SettingSimpleNode(name, value)
    }
}