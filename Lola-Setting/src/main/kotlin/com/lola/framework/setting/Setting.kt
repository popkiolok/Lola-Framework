package com.lola.framework.setting

import com.lola.framework.core.*
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.ForAnnotated
import com.lola.framework.core.decoration.findDecoration
import com.lola.framework.core.decoration.getDecoration
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Setting(val name: String, val info: String = "")

@ForAnnotated(Setting::class)
class SettingReference<T : Any>(override val target: LCallable<Any?, KProperty1<T, Any?>>, val data: Setting) :
    Decoration<LCallable<Any?, KProperty1<T, Any?>>> {
    fun getValue(instance: T, all: Boolean): SettingNode {
        val value = target.self.get(instance)
        val valueClass = value?.let { value::class.lola.findDecoration<SettingContainingClass<Any>>() }
        return if (valueClass != null) {
            val subtree = valueClass.getValues(value, all)
            if (all) {
                val subtrees = getSubclasses(target.self.returnType.jvmErasure.lola)
                    .filterNot { it.self.isSubclassOf(valueClass.target.self) || it.self.isAbstract }
                    .mapNotNull { it.findDecoration<SettingContainingClass<Any>>() }
                    .mapNotNull {
                        runCatching {
                            val inst = it.target.createInstance { ctx -> ctx.parents += instance.objectContext }
                            it.target.self.jvmName to it.getValues(inst, true)
                        }.getOrNull()
                    }.toMap()
                SettingMultiSubTreeNode(data.name, valueClass.target.self.jvmName, subtree, subtrees)
            } else {
                SettingSubTreeNode(data.name, valueClass.target.self.jvmName, subtree)
            }
        } else {
            SettingSimpleNode(data.name, value)
        }
    }

    fun setValue(instance: T, node: SettingNode) {
        when (node) {
            is SettingSimpleNode -> {
                if (target.self is KMutableProperty1<T, Any?>)
                    (target.self as KMutableProperty1<T, Any?>).set(instance, node.value)
            }

            is SettingSubTreeNode -> {
                val clazz = Class.forName(node.className).kotlin.lola.getDecoration<SettingContainingClass<Any>>()
                val curr = target.self.get(instance)
                if (curr != null && curr::class == clazz.target.self) {
                    clazz.setValues(curr, node.tree)
                } else {
                    if (target.self is KMutableProperty1<T, Any?>)
                        (target.self as KMutableProperty1<T, Any?>).set(
                            instance,
                            clazz.createWithSettings(instance.objectContext, node.tree)
                        )
                }
            }
        }
    }
}