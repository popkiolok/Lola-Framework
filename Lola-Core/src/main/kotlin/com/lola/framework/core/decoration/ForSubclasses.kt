package com.lola.framework.core.decoration

import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * Annotate decoration should be applied to every [LClass] that is subclass of [parent].
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForSubclasses(val parent: KClass<*>)

@ForAnnotated(ForSubclasses::class)
class ForSubclassesDecorator<T : Decoration<*>>(target: LClass<T>, ann: ForSubclasses) : DecorationClass<T>(target) {
    private val parent: KClass<*> = ann.parent

    init {
        Lola.decorate(object : ResolveClassListener<Lola> {
            override val target: Lola
                get() = Lola

            override fun <T : Any> onClassFound(clazz: LClass<T>) {
                if (clazz.self.isSubclassOf(parent)) {
                    val params = buildMap { put(targetParam.self, clazz) }
                    val decoration = target.createInstance(params)
                    clazz.decorate(decoration)
                }
            }
        })
    }
}