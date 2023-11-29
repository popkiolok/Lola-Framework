package com.lola.framework.event

import com.lola.framework.module.Module
import com.lola.framework.setting.Setting
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap

@Module(group = "Lola-Event", path = "EventSystem", info = "Stores active event listeners and allows to call events.")
class EventSystem(
    @Setting("Listener Exception Handler", info = "Action that handles listener execution exception.")
    val errorAction: (Throwable) -> Unit = { it.printStackTrace() }
) {
    /**
     * Number of active listeners in the [EventSystem].
     */
    val listeners: Int
        get() = attached.entries.sumOf { it.value.size }

    /**
     * Currently attached (active) event listeners. Event name to listeners for this event multimap.
     * Event name **must** be interned string.
     */
    internal val attached: MutableMap<String, MutableCollection<Pair<Any, ListenerFunction>>> = Reference2ObjectOpenHashMap()

    /**
     * Call event listeners of the event.
     *
     * @param event The name of event to call.
     * @param eventObject Some object with event data.
     * @return true if event is canceled, false otherwise.
     */
    fun call(event: String, eventObject: Any?): Boolean {
        val ls = attached[event] ?: return false
        val iterator = ls.iterator()
        val callback by lazy { ListenerCallback() }
        synchronized(ls) {
            while (iterator.hasNext()) {
                val (instance, listener) = iterator.next()
                try {
                    if (listener.target.parameters.size == 2) {
                        listener.target.call(instance, eventObject)
                    } else {
                        listener.target.call(instance, eventObject, callback)
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
        val ls = attached[event] ?: return false
        val iterator = ls.iterator()
        val callback by lazy { ListenerCallback() }
        synchronized(ls) {
            while (iterator.hasNext()) {
                val (instance, listener) = iterator.next()
                try {
                    if (listener.target.parameters.size == 1) {
                        listener.target.call(instance)
                    } else {
                        listener.target.call(instance, callback)
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