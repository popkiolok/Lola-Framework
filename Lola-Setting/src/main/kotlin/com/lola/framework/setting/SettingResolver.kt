package com.lola.framework.setting

import com.lola.framework.core.container.AddContainerListener
import com.lola.framework.core.container.Container
import com.lola.framework.core.container.subscribeAddContainerListener

object SettingResolver : AddContainerListener {

    init {
        subscribeAddContainerListener(this)
    }

    override fun onContainerAdded(container: Container) {
        container.decorate(SettingContainer(container))
    }
}