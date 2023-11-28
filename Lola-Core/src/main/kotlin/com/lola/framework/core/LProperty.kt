package com.lola.framework.core

import com.lola.framework.core.decoration.Decorated
import com.lola.framework.core.util.Option
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

interface LProperty<V> : KProperty<V>, LCallable<V>, Decorated {

    override val getter: Getter<V>

    /**
     * Returns if property has default value.
     */
    val hasDefaultValue: Boolean

    /**
     * Returns [Option] with default value of property, if possible to retrieve, empty [Option] otherwise.
     */
    val defaultValue: Option<Any?>

    /**
     * Found constructor parameters that are used to initialize this property.
     */
    val constructorParameters: Map<Function<Any>, LParameter>

    interface Getter<V> : KProperty.Getter<V>, LFunction<V>
}

interface LMutableProperty<V> : LProperty<V>, KMutableProperty<V> {

    override val setter: Setter<V>

    interface Setter<V> : KMutableProperty.Setter<V>, LFunction<Unit>
}