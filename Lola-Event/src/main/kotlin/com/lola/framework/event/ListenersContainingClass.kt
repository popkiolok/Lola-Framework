package com.lola.framework.event

import com.lola.framework.core.decoration.ForHavingDecoratedMembers
import com.lola.framework.core.LClass
import com.lola.framework.core.context.Context
import com.lola.framework.core.decoration.CreateInstanceListener
import com.lola.framework.core.decoration.hasDecoration
import com.lola.framework.core.lola
import com.lola.framework.core.objectContext
import com.lola.framework.module.*
import kotlin.Comparator
import java.util.*

@ForHavingDecoratedMembers(ListenerFunction::class)
class ListenersContainingClass<T : Any>(override val target: LClass<T>) : CreateInstanceListener<T> {
    override fun onCreateInstance(instance: T, context: Context) {
        val mis = context.mis
        mis.ifLoaded(EventSystem::class) { eventSystem ->
            target.getDecoratedMembers<ListenerFunction>().forEach {
                eventSystem.attached.getOrPut(it.data.event) {
                    Collections.synchronizedCollection(PriorityQueue(Comparator.comparingInt { (_, listener) ->
                        listener.data.priority.ordinal
                    }))
                }.add(instance to it)
            }
        }
        mis.onUnload = { it.detachListeners() }
    }
}

//@MemberForDecorated(ListenersContainingClass::class)
//@OnUnload
internal fun <T : Any> T.detachListeners() {
    if (this::class.lola.hasDecoration<ListenersContainingClass<*>>()) {
        objectContext.mis.ifLoaded(EventSystem::class) { eventSystem ->
            eventSystem.attached.values.forEach { it.removeIf { (inst, _) -> inst === this } }
        }
    }
}