package com.lola.framework.core.container

import com.lola.framework.core.Element
import com.lola.framework.core.constructor.Constructor
import com.lola.framework.core.container.context.Context
import com.lola.framework.core.container.context.ContextContainer
import com.lola.framework.core.container.decorations.AddConstructorListener
import com.lola.framework.core.container.decorations.AddFunctionListener
import com.lola.framework.core.container.decorations.AddPropertyListener
import com.lola.framework.core.container.decorations.CreateInstanceListener
import com.lola.framework.core.decoration.AbstractDecorated
import com.lola.framework.core.decoration.getDecorations
import com.lola.framework.core.function.Function
import com.lola.framework.core.function.parameter.Parameter
import com.lola.framework.core.log
import com.lola.framework.core.property.Property
import com.lola.framework.core.toJSON
import com.lola.framework.core.util.PLACEHOLDER
import com.lola.framework.core.util.SparseArray
import java.util.*

abstract class AbstractContainer : Container, AbstractDecorated<ContainerDecoration>() {
    override val superContainers: MutableList<AbstractContainer> = ArrayList()
    override val implementations: MutableCollection<AbstractContainer> = ArrayList()

    override val allProperties: Iterable<Property>
        get() = getAllElements<Property>(Container::properties)

    override val properties: MutableList<Property> = ArrayList()

    override val constructors: MutableCollection<Constructor> =
        PriorityQueue(Comparator.comparingInt { -it.parameters.size })

    override val functions: MutableCollection<Function> = ArrayList()

    override val allFunctions: Iterable<Function>
        get() = getAllElements<Function>(Container::functions)

    override val context = Context(mutableListOf(globalContext))

    protected fun afterInit() {
        var i = 0
        superContainers.forEach { container ->
            container.properties.forEach { prop ->
                if (isNotPresent(prop)) {
                    log.trace { "Adding element $prop to the container $this." }
                    assert(!properties.contains(prop))
                    properties.add(i, prop)
                    i++
                    getDecorations<AddPropertyListener>().forEach { it.onPropertyAdded(prop) }
                }
            }
        }

        addListeners.values.forEach { it.onContainerAdded(this) }

        decorate(ContextContainer(this))

        registered += this
        log.debug { "Creating container $this." }
    }

    override fun createInstance(
        params: Map<Parameter, Any?>,
        propertyValues: Map<Property, Any?>,
        ctxInitializer: (Context) -> Unit
    ): ContainerInstance {
        log.debug { "Creating instance of container $this with params: ${params.toJSON()}" }
        val ci = ContainerInstance(this)
        ci.context.register(Context::class, Context::class.java) { ci.context }
        ctxInitializer(ci.context)
        for (constructor in constructors) {
            log.trace { "Trying to construct with constructor $constructor." }
            val constructorParams = constructor.parameters
            val cParams = SparseArray(constructorParams.size) { index ->
                val param = constructorParams[index]
                if (params.containsKey(param)) {
                    params[param]
                } else PLACEHOLDER
            }
            val result = constructor.tryInvoke(ci, cParams)
            result.ifPresent { instance ->
                log.trace { "Created instance object $instance." }
                ci.instance0 = instance
                containerPool[instance] = ci

                log.trace { "Initializing properties for container $this." }
                properties.forEach { prop ->
                    if (prop.parameters.any { params.containsKey(it) }) {
                        return@forEach
                    }
                    if (propertyValues.containsKey(prop)) {
                        prop[ci] = propertyValues[prop]
                    } else {
                        prop.initializers.forEach {
                            it.supplyValue(ci.context).onSuccess { value ->
                                log.trace { "Initializing property $prop with value $value." }
                                prop[ci] = value
                            }.onFailure { e ->
                                log.warn { "Failed to supply value for property $prop with $it with reason: ${e.message}" }
                            }
                        }
                    }
                }

                getDecorations<CreateInstanceListener>().forEach { it.onCreateInstance(ci) }
                return ci
            }
        }
        log.error { "An error occurred while constructing container $this." }
        log.error { "No constructor applicable for parameters ${params.toJSON()}:" }
        constructors.forEach { log.error { " - $it" } }
        throw NullPointerException("No constructor present for the given parameters.")
    }

    override fun addElement(element: Element) {
        log.trace { "Adding element $element to the container $this." }
        when (element) {
            is Property -> addProperty(element)
            is Constructor -> addConstructor(element)
            is Function -> addFunction(element)
            else -> throw IllegalArgumentException(
                "Element should be Property, Constructor or Function. (Found $element)"
            )
        }
    }

    override fun addProperty(property: Property) {
        addElement(properties, property)
        getDecorations<AddPropertyListener>().forEach { it.onPropertyAdded(property) }
        implementations.forEach { impl -> impl.addCopyIfNotPresent(property) }
    }

    override fun addProperties(properties: Iterable<Property>) {
        addElements(this.properties, properties)
        val listeners = getDecorations<AddPropertyListener>()
        properties.forEach { property ->
            listeners.forEach { it.onPropertyAdded(property) }
            implementations.forEach { impl -> impl.addCopyIfNotPresent(property) }
        }
    }

    override fun addConstructor(constructor: Constructor) {
        addElement(constructors, constructor)
        getDecorations<AddConstructorListener>().forEach { it.onConstructorAdded(constructor) }
    }

    override fun addConstructors(constructors: Iterable<Constructor>) {
        addElements(this.constructors, constructors)
        constructors.forEach { constructor ->
            getDecorations<AddConstructorListener>().forEach { it.onConstructorAdded(constructor) }
        }
    }

    override fun addFunction(function: Function) {
        addElement(functions, function)
        getDecorations<AddFunctionListener>().forEach { it.onFunctionAdded(function) }
    }

    override fun addFunctions(functions: Iterable<Function>) {
        addElements(this.functions, functions)
        functions.forEach { function ->
            getDecorations<AddFunctionListener>().forEach { it.onFunctionAdded(function) }
        }
    }

    override fun decorate(decoration: ContainerDecoration) {
        super.decorate(decoration)

        // Without copying the collections ConcurrentModificationException will be thrown
        // if listener tries to add new element.
        if (decoration is AddFunctionListener) functions.toTypedArray().forEach { decoration.onFunctionAdded(it) }
        if (decoration is AddPropertyListener) properties.toTypedArray().forEach { decoration.onPropertyAdded(it) }
        if (decoration is AddConstructorListener) constructors.toTypedArray()
            .forEach { decoration.onConstructorAdded(it) }
    }

    private fun addCopyIfNotPresent(property: Property) {
        if (isNotPresent(property)) {
            addProperty(property.undecoratedCopy(ArrayList()))
        }
    }

    private fun isNotPresent(property: Property) = properties.none { it.name == property.name }

    private inline fun <reified T : Element> getAllElements(
        crossinline elements: Container.() -> Iterable<T>
    ): Iterable<T> {
        return (superContainers + sequenceOf(this)).flatMap { it.elements() }.asIterable()
    }

    private inline fun <reified T : Element> addElement(elements: MutableCollection<T>, element: T) {
        log.trace { "Adding element $element to the container $this." }
        assert(!elements.contains(element))
        elements += element
    }

    private inline fun <reified T : Element> addElements(elements: MutableCollection<T>, elementsToAdd: Iterable<T>) {
        assert(elementsToAdd.none { elements.contains(it) })
        if (log.isTraceEnabled()) {
            elementsToAdd.forEach { element ->
                log.trace { "Adding element $element to the container $this." }
            }
        }
        elements += elementsToAdd
    }

    override fun toString() = toJSON()

    override fun hasDefaultConstructor(): Boolean {
        return constructors.any { it.parameters.isEmpty() }
    }
}