package com.lola.framework.event

class ListenerCallback {
    internal var requestDetach = false
    internal var requestEventCancel = false

    /**
     * Detaches event listener from the [EventSystem].
     */
    fun detach() {
        requestDetach = true
    }

    /**
     * Cancel currently handled event.
     *
     */
    fun cancel() {
        requestEventCancel = true
    }

    fun cancelIf(condition: Boolean) {
        if (condition) {
            cancel()
        }
    }
}