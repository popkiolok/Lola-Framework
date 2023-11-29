package com.lola.framework.setting

import com.lola.framework.core.decoration.ResolveClassListener
import com.lola.framework.core.LClass
import com.lola.framework.core.container.subscribeAddContainerListener

object SettingResolver : ResolveClassListener {

    init {
        subscribeAddContainerListener(this)
    }

    override fun onClassFound(container: LClass) {
        container.decorate(SettingContainer(container))
    }
}