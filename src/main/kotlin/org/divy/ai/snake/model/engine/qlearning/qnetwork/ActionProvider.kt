package org.divy.ai.snake.model.engine.qlearning.qnetwork

import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeObservationModel

interface ActionProvider {
    fun suggestAction(observation: SnakeObservationModel): SnakeAction
}
