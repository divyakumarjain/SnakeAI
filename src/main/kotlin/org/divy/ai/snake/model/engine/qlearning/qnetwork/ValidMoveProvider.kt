package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeModel

interface ValidMoveProvider {
    fun getValidMoves(snake: SnakeModel): List<SnakeAction>
}
