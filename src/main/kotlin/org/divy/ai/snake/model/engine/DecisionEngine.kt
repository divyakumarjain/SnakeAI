package org.divy.ai.snake.model.engine

import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeObservationModel

interface DecisionEngine {
    fun output(observation: SnakeObservationModel): SnakeAction
}
