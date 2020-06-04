package org.divy.ai.snake.model.food

import org.divy.ai.snake.model.Position
import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType

class FoodEvent(type: EventType, val position: Position) : Event (type)
