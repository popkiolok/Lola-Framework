package com.lola.framework.core.kotlin

import com.lola.framework.core.Type
import com.lola.framework.core.annotation.AnnotationResolver
import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.property.AbstractProperty
import com.lola.framework.core.property.Property
import com.lola.framework.core.util.Option
import java.lang.IllegalStateException
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

class KotlinProperty : AbstractProperty {

    override val name: String
    override val type: KotlinType
    override val mutable: Boolean
    override val annotations: KotlinAnnotationResolver
    override val parameters: List<KotlinParameter>

    val kProperty: KProperty<*>

    override val hasDefaultValue: Boolean
        get() = !kProperty.isLateinit && (parameters.isEmpty() || parameters.any { it.isOptional })

    override val defaultValue: Option<Any?>
        get() = Option.empty() // TODO

    constructor(kProperty: KProperty<*>, parameters: List<KotlinParameter>) :
            this(
                kProperty,
                parameters,
                kProperty.name,
                KotlinType(kProperty.returnType),
                kProperty is KMutableProperty,
                KotlinAnnotationResolver(kProperty)
            ) {
        kProperty.getter.isAccessible = true
        if (kProperty is KMutableProperty) {
            kProperty.setter.isAccessible = true
        }
    }

    private constructor(
        kProperty: KProperty<*>,
        parameters: List<KotlinParameter>,
        name: String,
        type: KotlinType,
        mutable: Boolean,
        annotations: KotlinAnnotationResolver
    ) {
        this.kProperty = kProperty
        this.parameters = parameters
        this.name = name
        this.type = type
        this.mutable = mutable
        this.annotations = annotations
    }

    override fun getValue(instance: ContainerInstance): Any? {
        return kProperty.getter.call(instance.instance)
    }

    override fun setValue(instance: ContainerInstance, value: Any?) {
        if (kProperty !is KMutableProperty) {
            throw IllegalStateException("Kotlin Property $this is not mutable.")
        }
        kProperty.setter.call(instance.instance, value)
    }

    @Suppress("UNCHECKED_CAST") // TODO
    override fun undecoratedCopy(parameters: List<Parameter>): KotlinProperty {
        return KotlinProperty(kProperty, parameters as List<KotlinParameter>, name, type, mutable, annotations)
    }
}