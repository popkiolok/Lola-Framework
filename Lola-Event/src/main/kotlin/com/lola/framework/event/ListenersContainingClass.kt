package com.lola.framework.event

import com.lola.framework.core.decoration.ForHavingDecoratedMembers
import com.lola.framework.core.decoration.MemberForDecorated
import com.lola.framework.core.LClass
import com.lola.framework.core.container.context
import com.lola.framework.core.container.context.Context
import com.lola.framework.core.decoration.CreateInstanceListener
import com.lola.framework.core.getDecoratedMembers
import com.lola.framework.core.kotlin.lola
import com.lola.framework.module.*
import kotlin.Comparator
import java.util.*

@ForHavingDecoratedMembers(ListenerFunction::class)
class ListenersContainingClass<T : Any>(override val self: LClass<T>) : CreateInstanceListener<T> {
    override fun onCreateInstance(instance: T, context: Context) {
        context.mis.ifLoaded(EventSystem::class) { eventSystem ->
            self.getDecoratedMembers<ListenerFunction>().forEach {
                eventSystem.attached.getOrPut(it.info.event) {
                    Collections.synchronizedCollection(PriorityQueue(Comparator.comparingInt { (_, listener) ->
                        listener.info.priority.ordinal
                    }))
                }.add(instance to it)
            }
        }
    }
}

@OnUnload
@MemberForDecorated(ListenersContainingClass::class)
internal fun <T : Any> T.detachListeners() {
    if (this::class.lola.hasDecoration(ListenersContainingClass::class)) {
        context.mis.ifLoaded(EventSystem::class) { eventSystem ->
            eventSystem.attached.values.forEach { it.removeIf { (inst, _) -> inst === this } }
        }
    }
}