package org.divy.ai.snake.model.snake.event

import org.divy.ai.snake.model.game.Event
import org.divy.ai.snake.model.game.EventType
import org.divy.ai.snake.model.snake.SnakeModel
import org.divy.ai.snake.model.snake.SnakeObservationModel

class SnakeMoveCompleted(
    val snake: SnakeModel,
    val observation: SnakeObservationModel
) : Event(EventType.SNAKE_MOVE_COMPLETED) {

}
