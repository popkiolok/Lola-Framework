package com.lola.framework.core.kotlin.parametersresolving

import com.lola.framework.core.kotlin.KotlinParameter
import kotlin.reflect.KProperty

/**
 * Interface for resolving constructor parameters which initialize a property.
 */
interface PropertyParametersResolver {
    /**
     * Resolve constructor parameters which initialize a property.
     *
     * @param allParameters All parameters of all constructors exists.
     * @param property The [KProperty] to be initialized.
     * @return Resolved constructor parameters which initialize given [property].
     */
    fun resolve(allParameters: Iterable<KotlinParameter>, property: KProperty<*>): Iterable<KotlinParameter>
}