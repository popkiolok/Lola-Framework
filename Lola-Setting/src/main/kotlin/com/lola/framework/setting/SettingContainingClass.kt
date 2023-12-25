package com.lola.framework.setting

import com.lola.framework.core.*
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.Decoration
import com.lola.framework.core.decoration.ForHavingDecoratedMembers
import com.lola.framework.core.decoration.getDecoration
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

@ForHavingDecoratedMembers(SettingReference::class)
class SettingContainingClass<T : Any>(override val target: LClass<T>) : Decoration<LClass<T>> {
    /**
     * Recursively retrieves a flat map of all setting values in the class and its superclasses.
     * If some setting value type has [SettingContainingClass] decoration, value at its path in the returned map will be
     * current value [KClass.jvmName] and subtree will be created for settings in it.
     *
     * @param instance An instance of the [target].
     * @return A map of setting absolute paths to their corresponding values.
     */
    fun getValues(instance: T): Collection<SettingNode> {
        return getValues(instance, false)
    }

    /**
     * [getValues] that also gets default values of settings for all existing subclasses of
     * settings properties, which types have [SettingContainingClass] decoration, types.
     *
     * @param instance The instance of the [target].
     * @return A map of setting absolute paths to their corresponding values.
     */
    fun getAllValues(instance: T): Collection<SettingNode> {
        return getValues(instance, true)
    }

    /**
     * Recursively retrieves the values of settings in the class.
     *
     * @param instance Instance of class contains settings.
     * @param all Flag to indicate whether to retrieve values for all subclasses of property type for properties in class.
     */
    fun <V : Any> getValues(
        instance: V, all: Boolean = false
    ): Collection<SettingNode> {
        return target.getDecoratedMembers<SettingReference<V>>().map { it.getValue(instance, all) }.toList()
    }

    /**
     * Recursively sets settings values in [target] class to the given.
     * If some setting in [target] class is immutable, no values will be changed.
     * If some setting in tree not in [target] class is immutable, whole class containing this setting will be recreated.
     * If [values] contain value for non-existing setting, it will be ignored.
     * If [values] doesn't contain value for some existing setting, it will not be changed.
     *
     * @param instance The instance of the container.
     * @param values Setting values tree.
     * @return True, if all settings declared in [target] should be changed were mutable and values
     * were set successfully, false otherwise.
     */
    // TODO: currently does not implement expected behaviour correctly
    fun setValues(instance: T, values: Collection<SettingNode>) {
        target.getDecoratedMembers<SettingReference<T>>().forEach { setting ->
            val node = values.firstOrNull { it.name == setting.data.name } ?: return@forEach
            setting.setValue(instance, node)
        }
    }

    fun createWithSettings(
        context: Context,
        values: Collection<SettingNode>,
        params: Map<KParameter, Any?> = emptyMap(),
        propertyValues: Map<KProperty<*>, Any?> = emptyMap(),
        ctxInitializer: (Context) -> Unit = {}
    ) {
        target.createInstance(params, propertyValues + getPropertyValuesMapFrom(context, values)) {
            it.parents += context
            ctxInitializer(it)
        }
    }

    fun getPropertyValuesMapFrom(context: Context, values: Collection<SettingNode>): Map<KProperty<*>, Any?> {
        val settings = target.getDecoratedMembers<SettingReference<*>>().associateBy { it.data.name }
        return values.mapNotNull { node ->
            val setting = settings[node.name] ?: return@mapNotNull null
            (setting.target.self as KProperty<*>) to when (node) {
                is SettingSimpleNode -> node.value
                is SettingSubTreeNode -> {
                    val clazz = Class.forName(node.className).kotlin.lola.getDecoration<SettingContainingClass<Any>>()
                    clazz.createWithSettings(context, node.tree)
                }
                else -> throw IllegalArgumentException()
            }
        }.toMap()
    }
}