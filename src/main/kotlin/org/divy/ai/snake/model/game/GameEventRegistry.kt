package org.divy.ai.snake.model.game

import java.util.*
import kotlin.collections.ArrayList

class GameEventRegistry {

    private val registry: MutableMap<EventType, MutableList<GameEventListener>> = EnumMap(EventType::class.java)

    fun raiseEvent(event: Event) {
        registry[event.type]?.forEach {
            it.handle(event)
        }
    }

    fun addEventListener(type: EventType, listener: GameEventListener) {
        if(!registry.containsKey(type)) {
            registry[type] = ArrayList()
        }
        registry[type]?.add(listener)
    }
}

interface GameEventListener {
    fun handle(event: Event)
}
