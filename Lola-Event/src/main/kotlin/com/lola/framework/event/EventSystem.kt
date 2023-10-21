package com.lola.framework.event

import com.google.common.collect.*
import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.container.subscribeAddContainerListener
import com.lola.framework.module.Module
import com.lola.framework.setting.Setting
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
import java.util.*

@Module(group = "Lola-Event", path = "EventSystem", info = "Stores active event listeners and allows to call events.")
class EventSystem(
    @Setting("Listener Exception Handler", info = "Action that handles listener execution exception.")
    val errorAction: (Throwable) -> Unit = { it.printStackTrace() }
) {
    /**
     * Number of active listeners in the [EventSystem].
     */
    val listeners: Int
        get() = attached.asMap().map { it.value.size }.sum()

    /**
     * Currently attached (active) event listeners. Event name to listeners for this event multimap.
     * Event name **must** be interned string.
     */
    internal val attached: Multimap<String, Pair<ContainerInstance, EventListener>> =
        Multimaps.newMultimap(Reference2ObjectOpenHashMap<String, Collection<Pair<ContainerInstance, EventListener>>>()) {
            Collections.synchronizedCollection(PriorityQueue(Comparator.comparingInt { (_, listener) ->
                listener.priority
            }))
        }

    constructor() : this({ it.printStackTrace() })

    init {
        subscribeAddContainerListener(ListenerResolver)
    }

    /**
     * Call event listeners of the event.
     *
     * @param event The name of event to call.
     * @param eventObject Some object with event data.
     * @return true if event is canceled, false otherwise.
     */
    fun call(event: String, eventObject: Any?): Boolean {
        val ls = attached[event]
        val iterator = ls.iterator()
        val callback by lazy { ListenerCallback() }
        synchronized(ls) {
            while (iterator.hasNext()) {
                val (instance, listener) = iterator.next()
                try {
                    if (listener.self.parameters.size == 1 + 1) {
                        listener.self.invoke(instance, arrayOf(eventObject))
                    } else {
                        listener.self.invoke(instance, arrayOf(eventObject, callback))
                        if (callback.requestDetach) {
                            iterator.remove()
                            callback.requestDetach = false
                        }
                        if (callback.requestEventCancel) {
                            return true
                        }
                    }
                } catch (t: Throwable) {
                    errorAction(t)
                }
            }
        }
        return false
    }

    /**
     * Call event listeners of the event.
     *
     * @param event The name of event to call.
     * @return true if event is canceled, false otherwise.
     */
    fun call(event: String): Boolean {
        val ls = attached[event]
        val iterator = ls.iterator()
        synchronized(ls) {
            val callback by lazy { ListenerCallback() }
            while (iterator.hasNext()) {
                val (instance, listener) = iterator.next()
                try {
                    if (listener.self.parameters.size == 1) {
                        listener.self.invoke(instance, arrayOf())
                    } else {
                        listener.self.invoke(instance, arrayOf(callback))
                        if (callback.requestDetach) {
                            iterator.remove()
                            callback.requestDetach = false
                        }
                        if (callback.requestEventCancel) {
                            return true
                        }
                    }
                } catch (t: Throwable) {
                    errorAction(t)
                }
            }
        }
        return false
    }
}