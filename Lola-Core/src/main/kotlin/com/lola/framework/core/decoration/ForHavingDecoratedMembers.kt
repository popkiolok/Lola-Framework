package com.lola.framework.core.decoration

import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ForHavingDecoratedMembers(val decoration: KClass<out Decoration<*>>)

@ForAnnotated(ForHavingDecoratedMembers::class)
class ForHavingDecoratedMembersDecorator<T : Decoration<*>>(target: LClass<T>, ann: ForHavingDecoratedMembers) :
    DecorationClass<T>(target) {
    private val decoration = ann.decoration

    init {
        Lola.decorate(object : DecorateClassListener<Lola> {
            override val target: Lola
                get() = Lola

            override fun <T : Any> onDecoratedClass(decoration: Decoration<LClass<T>>) {
                if (decoration::class.isSubclassOf(this@ForHavingDecoratedMembersDecorator.decoration)) {
                    decoration.target.decorate(target.createInstance { it["DecorationTarget"] = decoration.target })
                }
            }
        })
    }
}