package com.lola.framework.core.kotlin.parametersresolving

/**
 * Annotate property to associate with parameter that initialize it.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class InitializedBy(
    /**
     * Name of parameter initializes property. If empty, [paramIndex] will be used.
     */
    val paramName: String = "",
    /**
     * Index of parameter initializes property. Considered only if [paramName] is empty.
     * If class has multiple constructors, parameters initialize the property should be on the same index.
     */
    val paramIndex: Int = -1)
