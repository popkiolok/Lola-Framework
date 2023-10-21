package com.lola.framework.core.kotlin.parametersresolving

import com.lola.framework.core.kotlin.KotlinParameter
import com.lola.framework.core.kotlin.KotlinProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

/**
 * [PropertyParametersResolver] which associate property and parameters using [InitializedBy] property annotation.
 */
object AnnotationPropertyParametersResolver : PropertyParametersResolver {
    override fun resolve(
        allParameters: Iterable<KotlinParameter>,
        property: KProperty<*>
    ): Iterable<KotlinParameter> {
        val propAnnotation = property.findAnnotation<InitializedBy>() ?: return listOf()
        return allParameters.filter {
            it.name == propAnnotation.paramName || it.kParameter.index == propAnnotation.paramIndex
        }
    }
}