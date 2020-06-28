package org.divy.ai.snake.model.engine

import org.divy.ai.snake.model.snake.SnakeAction
import org.divy.ai.snake.model.snake.SnakeObservationModel

class ProxyBrain : DecisionEngine {

    lateinit var original: DecisionEngine
    override fun output(observation: SnakeObservationModel): SnakeAction {
        return original.output(observation)
    }
}
