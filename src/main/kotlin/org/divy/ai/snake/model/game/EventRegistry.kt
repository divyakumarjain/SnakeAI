package org.divy.ai.snake.model.game

import java.util.*
import kotlin.collections.ArrayList

class EventRegistry private constructor(private val parentEventRegistry: EventRegistry? = null) {

    private val registry: MutableMap<EventType, MutableList<GameEventListener>> = EnumMap(EventType::class.java)

    fun raiseEvent(event: Event) {
        registry[event.type]?.forEach {
            it.handleEvent(event)
        }

        parentEventRegistry?.raiseEvent(event)
    }

    fun addEventListener(type: EventType, listener: GameEventListener) {
        if(!registry.containsKey(type)) {
            registry[type] = ArrayList()
        }
        val listenerList = registry[type]
        if (listenerList != null) {
            if(listenerList.indexOf(listener) < 0)
                listenerList.add(listener)
        }
    }

    private object HOLDER {
        val INSTANCE = EventRegistry()
    }

    companion object {
        val instance: EventRegistry by lazy { HOLDER.INSTANCE }

        fun createChildInstance(): EventRegistry {
            return EventRegistry(HOLDER.INSTANCE)
        }
    }
}

interface GameEventListener {
    fun handleEvent(event: Event)
}
