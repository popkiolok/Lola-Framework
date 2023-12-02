package com.lola.framework.core.decoration

import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import com.lola.framework.core.Lola
import kotlin.reflect.KCallable
import kotlin.reflect.full.isSubclassOf

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