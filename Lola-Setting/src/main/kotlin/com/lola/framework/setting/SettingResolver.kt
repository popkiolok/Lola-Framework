package com.lola.framework.setting

import com.lola.framework.core.decoration.FoundClassListener
import com.lola.framework.core.LClass
import com.lola.framework.core.container.subscribeAddContainerListener

object SettingResolver : FoundClassListener {

    init {
        subscribeAddContainerListener(this)
    }

    override fun onClassFound(container: LClass) {
        container.decorate(SettingContainer(container))
    }
}