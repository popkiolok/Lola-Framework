package com.lola.framework.module

import com.lola.framework.core.Path
import com.lola.framework.core.container.Container
import com.lola.framework.core.container.ContainerDecoration
import com.lola.framework.core.container.decorations.AddFunctionListener
import com.lola.framework.core.function.Function
import java.lang.IllegalStateException

class ModuleContainer(override val self: Container) : ContainerDecoration, AddFunctionListener,
    DependenciesUsingContainer {
    val group: String
    val path: Path<String>
    val name: String
    val info: String

    init {
        val ann = self.annotations.getAnnotation(Module::class)
        group = ann.group
        path = Path(ann.path.split('.'))
        name = "$group:$path"
        info = ann.info
    }

    override fun onFunctionAdded(function: Function) {
        val isOnLoad = function.annotations.hasAnnotation(OnLoad::class)
        val isOnUnload = function.annotations.hasAnnotation(OnUnload::class)
        if (isOnLoad || isOnUnload) {
            if (function.parameters.size > 1) {
                throw IllegalStateException("Function annotated as OnLoad or OnUnload function must not have parameters except instance. But function $function in $self has ${function.parameters.size} parameters.")
            }
        }
        if (isOnLoad) {
            function.decorate(OnLoadFunction(function))
        }
        // Function can be OnLoad and OnUnload function at the same time
        if (isOnUnload) {
            function.decorate(OnUnloadFunction(function))
        }
    }
}