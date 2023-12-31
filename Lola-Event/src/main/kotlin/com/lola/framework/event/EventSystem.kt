package com.lola.framework.event

import com.lola.framework.module.Module
import com.lola.framework.setting.Setting
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap

@Module(name = "Lola-Event:EventSystem", info = "Stores active event listeners and allows to call events.")
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
     */
    internal val attached: MutableMap<String, MutableCollection<Pair<Any, ListenerFunction>>> = HashMap()

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
        synchronized(ls) {
            var callback: ListenerCallback? = null
            while (iterator.hasNext()) {
                val (instance, listener) = iterator.next()
                try {
                    if (listener.target.self.parameters.size == 2) {
                        listener.target.self.call(instance, eventObject)
                    } else {
                        if (callback == null) callback = ListenerCallback()
                        listener.target.self.call(instance, eventObject, callback)
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
        synchronized(ls) {
            var callback: ListenerCallback? = null
            while (iterator.hasNext()) {
                val (instance, listener) = iterator.next()
                try {
                    if (listener.target.self.parameters.size == 1) {
                        listener.target.self.call(instance)
                    } else {
                        if (callback == null) callback = ListenerCallback()
                        listener.target.self.call(instance, callback)
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