package com.lola.framework.core.decoration

import com.lola.framework.core.LCallable
import com.lola.framework.core.LClass
import kotlin.reflect.KCallable
import kotlin.reflect.full.isSubclassOf

@ForAnnotated(ForHavingDecoratedMembers::class)
class ForHavingDecoratedMembersDecorator<T : Decoration<*>>(target: LClass<T>, ann: ForHavingDecoratedMembers) :
    DecorationClass<T>(target), DecorateClassMemberListener<LClass<T>> {
    private val decoration = ann.decoration

    override fun <T : Any> onDecoratedClassMember(
        clazz: LClass<T>,
        member: LCallable<*, KCallable<*>>,
        decoration: Decoration<LCallable<*, KCallable<*>>>
    ) {
        if (decoration::class.isSubclassOf(this.decoration)) {
            clazz.decorate(target.createInstance { it["DecorationTarget"] = clazz })
        }
    }
}