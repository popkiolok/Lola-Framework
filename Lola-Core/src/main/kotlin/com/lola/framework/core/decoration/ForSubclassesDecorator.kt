package com.lola.framework.core.decoration

import com.lola.framework.core.LClass
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@ForAnnotated(ForSubclasses::class)
class ForSubclassesDecorator<T : Decoration<*>>(target: LClass<T>, ann: ForSubclasses) : DecorationClass<T>(target),
    ResolveClassAnywhereListener<LClass<T>> {
    private val parent: KClass<*> = ann.parent

    override fun <T : Any> onClassFoundAnywhere(clazz: LClass<T>) {
        if (clazz.self.isSubclassOf(parent)) {
            clazz.decorate(target.createInstance { it["DecorationTarget"] = clazz })
        }
    }
}