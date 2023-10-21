package com.lola.framework.core.kotlin.parametersresolving

import com.lola.framework.core.kotlin.KotlinParameter
import com.lola.framework.core.kotlin.KotlinProperty
import kotlin.reflect.KProperty

/**
 * [PropertyParametersResolver] which associate property and parameters which names are the same as property name.
 */
object NamePropertyParametersResolver : PropertyParametersResolver {
    override fun resolve(
        allParameters: Iterable<KotlinParameter>,
        property: KProperty<*>
    ): Iterable<KotlinParameter> {
        return allParameters.filter { it.name == property.name }
    }
}