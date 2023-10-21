package com.lola.framework.event

import com.lola.framework.core.container.Container
import com.lola.framework.core.container.ContainerInstance
import com.lola.framework.core.container.decorations.AddFunctionListener
import com.lola.framework.core.container.decorations.CreateInstanceListener
import com.lola.framework.core.container.getContainerInstance
import com.lola.framework.core.decoration.getDecoration
import com.lola.framework.core.decoration.hasDecoration
import com.lola.framework.core.function.Function
import com.lola.framework.core.kotlin.KotlinFunction
import com.lola.framework.core.kotlin.getKotlinContainer
import com.lola.framework.module.*

class EventListenerContainer(override val self: Container) : CreateInstanceListener, AddFunctionListener {
    private var hasEventListeners: Boolean = false

    override fun onCreateInstance(instance: ContainerInstance) {
        if (!hasEventListeners) return
        (instance.context[ModuleInstanceStorage::class] as ModuleInstanceStorage)
            .ifLoaded(getKotlinContainer(EventSystem::class)!!.asModule) { eventSystem ->
                self.functions.forEach {
                    val el = it.getDecoration<EventListener>()
                    if (el != null) {
                        (eventSystem.instance as EventSystem).attached.put(el.event, instance to el)
                    }
                }
            }
    }

    override fun onFunctionAdded(function: Function) {
        if (function.annotations.hasAnnotation(Listener::class)) {
            function.decorate(EventListener(function))
            if (!hasEventListeners) {
                if (self.isModule) {
                    self.addFunction(KotlinFunction(::onUnloadModule))
                }
                hasEventListeners = true
            }
        }
    }
}

@OnUnload
internal fun onUnloadModule(self: Any) {
    val ci = getContainerInstance(self)
    if (ci.container.hasDecoration<EventListenerContainer>()) {
        val mis = ci.context[ModuleInstanceStorage::class] as ModuleInstanceStorage
        mis.ifLoaded(ModuleRegistry["Lola-Event:EventSystem"]) { eventSystemInst ->
            val eventSystem = eventSystemInst.instance as EventSystem
            eventSystem.attached.values().removeIf { (inst, _) -> inst == ci }
        }
    }
}