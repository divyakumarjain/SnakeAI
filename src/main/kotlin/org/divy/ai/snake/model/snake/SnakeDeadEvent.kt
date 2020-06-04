package org.divy.ai.snake.model.snake

import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType

class SnakeDeadEvent(val snakeModel: SnakeModel) : Event(EventType.SNAKE_DEAD){
}
