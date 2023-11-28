package com.lola.framework.module

import com.lola.framework.core.decoration.ForAnnotated
import com.lola.framework.core.Path
import com.lola.framework.core.LClass
import com.lola.framework.core.container.ContainerDecoration

@ForAnnotated(Module::class)
class ModuleContainer<T : Any>(override val self: LClass<T>, ann: Module) : ContainerDecoration {
    val group: String
    val path: Path<String>
    val name: String
    val info: String

    init {
        group = ann.group
        path = Path(ann.path.split('.'))
        name = "$group:$path"
        info = ann.info
    }
}