package com.lola.framework.setting

import com.lola.framework.core.*

/**
 * Registers [SettingProperty]s for container and provides access to their values.
 */
/*class SettingContainer(override val self: LClass) : Decoration<Con{

    /**
     * Recursively retrieves a flat map of all setting values in the container and its super containers.
     * If some setting is container, value at its path in map will be
     * an instance of [LClass] class for the container associated with current property value.
     * This container must be implementation of [LClass] from [LProperty.type] [LType.clazz].
     *
     * @param instance The instance of the container.
     * @return A map of setting absolute paths to their corresponding values.
     */
    fun getValues(instance: ContainerInstance): Map<Path<String>, Any?> {
        val map: MutableMap<Path<String>, Any?> = HashMap()
        fillValuesRelative(instance, emptyPath(), map)
        return map
    }

    /**
     * [getValues] that also gets default values of settings of all possible (not only current)
     * implementations of settings with container instances as values.
     *
     * @param instance The instance of the container.
     * @return Setting absolute path to value map.
     */
    fun getAllValues(instance: ContainerInstance): Map<Path<String>, Any?> {
        val map: MutableMap<Path<String>, Any?> = HashMap()
        fillValuesRelative(instance, emptyPath(), map, true)
        return map
    }

    /**
     * Recursively retrieves the values of settings in the container and
     * its super containers (optionally) relative to a parent path.
     *
     * @param instance The instance of the container.
     * @param parent The parent path.
     * @param dest The destination map to store setting values.
     * @param all Flag to indicate whether to retrieve values for all containers implementations.
     * @param allProps Flag to indicate whether to include super containers settings values.
     * @param useDefaults Flag to indicate whether to use default values for properties or the values from the [instance].
     */
    private fun fillValuesRelative(
        instance: ContainerInstance?, parent: Path<String>,
        dest: MutableMap<Path<String>, Any?>, all: Boolean = false,
        allProps: Boolean = true, useDefaults: Boolean = false
    ) {
        for (prop in if (allProps) self.allProperties else self.properties) {
            prop.setting?.let { setting ->
                val value = if (useDefaults) {
                    val dv = prop.defaultValue
                    if (dv.isEmpty) {
                        return@let
                    }
                    dv.get()
                } else prop[instance!!]
                val path = parent + setting.name
                val containerInst by lazy { value?.let { getContainerInstance(value) } }
                val container = prop.type?.clazz ?: containerInst?.container
                if (container == null || value == null) {
                    dest[path] = value
                } else {
                    val inst = containerInst!!
                    val instContainer = inst.container
                    dest[path] =
                        instContainer // TODO serialization of container, property, function etc. with getting container from existing by some serialized value
                    if (all) {
                        instContainer.settings?.fillValuesRelative(inst, path, dest, true)
                        (container.implementations.asSequence() - instContainer).forEach {
                            (it.superContainers.asSequence() - container).forEach { sc ->
                                sc.settings?.fillValuesRelative(
                                    null, path, dest, true,
                                    allProps = false, useDefaults = true
                                )
                            }
                            it.settings?.fillValuesRelative(
                                null, path, dest, true,
                                allProps = false, useDefaults = true
                            )
                        }
                    } else {
                        instContainer.settings?.fillValuesRelative(inst, path, dest)
                    }
                }
            }
        }
    }


    /**
     * Recursively sets settings with given values in the container and its super containers.
     * If some top-level setting (top-level setting - setting for property in the current container,
     * but not for some property in some container which is value of setting in the current container)
     * is immutable, no values will be changed. If not top-level setting is immutable,
     * whole container containing this setting will be recreated.
     * If [values] contain value for non-existing setting, it will be ignored. If [values] doesn't
     * contain value for some existing setting, it will not be changed.
     *
     * @param instance The instance of the container.
     * @param values Setting absolute path to value map.
     * @return True, if all top-level settings should be changed were mutable and values
     * were set successfully, false otherwise.
     * @throws TypeCastException If value for container setting path is not [LClass].
     */
    fun setValues(instance: ContainerInstance, values: Map<Path<String>, Any?>): Boolean {
        return setValuesRelative(instance, values, emptyPath())
    }

    private fun setValuesRelative(
        instance: ContainerInstance, values: Map<Path<String>, Any?>,
        parent: Path<String>, ignoreImmutable: Boolean = false
    ): Boolean {
        val propToSettingPath = self.allProperties.mapNotNull { prop ->
            val setting = prop.setting
            if (setting == null) {
                null
            } else {
                val path = parent + setting.name
                if (!values.containsKey(path)) {
                    return@mapNotNull null
                }
                if (prop.immutable) {
                    if (ignoreImmutable) {
                        return@mapNotNull null
                    }
                    return false
                }
                prop to path
            }
        }

        for ((prop, path) in propToSettingPath) {
            val currValue = prop[instance]
            val propVal = initProp(values, path, currValue)
            if (propVal !== currValue) {
                prop[instance] = propVal
            }
        }
        return true
    }

    private fun initProp(
        values: Map<Path<String>, Any?>, path: Path<String>, currValue: Any?
    ): Any? {
        val value = values[path]
        val currContainerInst by lazy { getContainerInstance(currValue) }
        val container = if (value is LClass) value else null

        return if (container == null) {
            value
        } else {
            val newContainer = value as LClass
            //         if same implementation
            val inst = if (newContainer == currContainerInst?.container) {
                currContainerInst!!
            } else {
                makeContainer(newContainer, values, path)
            }
            newContainer.settings?.setValuesRelative(inst, values, path, true)
            inst
        }
    }

    private fun makeContainer(
        newContainer: LClass, values: Map<Path<String>, Any?>,
        path: Path<String>
    ): ContainerInstance {
        val params = HashMap<LParameter, Any?>()
        newContainer.allProperties.forEach { prop ->
            val setting = prop.setting
            if (setting != null) {
                val propParams = prop.parameters
                if (propParams.isNotEmpty()) {
                    val v = initProp(values, path + setting.name, null)
                    propParams.forEach { params[it] = v }
                }
            }
        }
        return newContainer.createInstance(params)
    }
}*/