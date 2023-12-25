package com.lola.framework.module

import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.ForAll
import com.lola.framework.core.decoration.ResolveClassListener
import java.util.PriorityQueue
import kotlin.reflect.full.isSubclassOf

interface ModuleInstanceSupplier {
    val priority: Double

    fun <T : Any> get(moduleClass: ModuleClass<T>, context: Context): T?
}

val moduleInstanceSuppliers: Collection<ModuleInstanceSupplier> = PriorityQueue(compareByDescending { it.priority })

@ForAll
internal class SuppliersResolver(override val target: Lola) : ResolveClassListener<Lola> {
    override fun <T : Any> onClassFound(clazz: LClass<T>) {
        if (clazz.self.isSubclassOf(ModuleInstanceSupplier::class) && !clazz.self.isAbstract) {
            (moduleInstanceSuppliers as PriorityQueue) += clazz.createInstance() as ModuleInstanceSupplier
        }
    }
}