package com.lola.framework.core.decoration

import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@ForAnnotated(ForSubclasses::class)
class ForSubclassesDecorator<T : Decoration<*>>(target: LClass<T>, ann: ForSubclasses) : DecorationClass<T>(target) {
    private val parent: KClass<*> = ann.parent

    init {
        Lola.decorate(object : ResolveClassListener<Lola> {
            override val target: Lola
                get() = Lola

            override fun <T : Any> onClassFound(clazz: LClass<T>) {
                if (clazz.self.isSubclassOf(parent)) {
                    clazz.decorate(target.createInstance { it["DecorationTarget"] = clazz })
                }
            }
        })
    }
}