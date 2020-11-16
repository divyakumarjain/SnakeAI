package org.divy.ai.snake.model.snake.event

import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType
import org.divy.ai.snake.model.snake.SnakeModel

class SnakeDeadEvent(val snakeModel: SnakeModel, val deathType: SnakeDeathType) : Event(EventType.SNAKE_DEAD){
}

enum class SnakeDeathType {
    WALL_COLLIDE,
    BODY_COLLIDE,
    STARVED
}
