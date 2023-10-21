package com.lola.framework.event

import com.lola.framework.core.container.Container
import com.lola.framework.core.container.AddContainerListener

object ListenerResolver : AddContainerListener {
    override fun onContainerAdded(container: Container) {
        container.decorate(EventListenerContainer(container))
    }
}