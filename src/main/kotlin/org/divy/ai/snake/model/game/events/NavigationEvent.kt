package org.divy.ai.snake.model.game.events

import javafx.scene.input.KeyEvent
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType

class NavigationEvent(type: EventType, val keyEvent: KeyEvent) : Event(type)
